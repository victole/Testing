package com.despegar.sobek.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.despegar.framework.persistence.hibernate.dao.generic.query.OrderType;
import com.despegar.library.rest.connector.exceptions.ServiceError;
import com.despegar.library.rest.connector.exceptions.ServiceException;
import com.despegar.framework.utils.string.StringUtils;
import com.despegar.sobek.dao.BenefitDAO;
import com.despegar.sobek.dao.CompanyDAO;
import com.despegar.sobek.dto.CompanyDTO;
import com.despegar.sobek.dto.CompanyFilterDTO;
import com.despegar.sobek.dto.CompanyIdentifierDTO;
import com.despegar.sobek.dto.CompanySearchResultContainerDTO;
import com.despegar.sobek.dto.CompanySearchResultDTO;
import com.despegar.sobek.dto.PictureDTO;
import com.despegar.sobek.dto.CompanyFilterDTO.OrderDirectionType;
import com.despegar.sobek.exception.SobekServiceException;
import com.despegar.sobek.model.Company;
import com.despegar.sobek.service.CompanyService;
import com.despegar.sobek.solr.index.manager.BenefitIndexManager;
import com.despegar.sobek.translator.CompanyByCriteriaTranslator;
import com.despegar.sobek.translator.CompanyIdentifierTranslator;
import com.despegar.sobek.translator.CompanyTranslator;
import com.google.common.collect.Lists;

public class CompanyServiceImpl
    implements CompanyService {

    private final Logger logger = Logger.getLogger(this.getClass());

    private CompanyDAO companyDAO;
    private CompanyTranslator companyTranslator;
    private CompanyByCriteriaTranslator companyByCriteriaTranslator;
    private CompanyIdentifierTranslator companyIdentifierTranslator;
    private Map<String, String> mapCompanySortingValues;
    private BenefitIndexManager benefitIndexManager;
    private BenefitDAO benefitDAO;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public CompanyDTO getCompanyByOID(final Long companyOID) {
        this.logger.info(StringUtils.concat("Getting company for OID: ", companyOID));

        this.validateCompanyOID(companyOID);

        Company company = CompanyServiceImpl.this.companyDAO.getCompanyByOID(companyOID);
        if (company == null) {
            throw new SobekServiceException(StringUtils.concat("No Company with OID: ", companyOID, " exists"));
        }
        this.logger.info(StringUtils.concat("Returning company for OID: ", companyOID));
        return CompanyServiceImpl.this.companyTranslator.getDTO(company);
    }

    private void validateCompanyOID(Long companyOID) {
        if (companyOID == null) {
            ServiceError serviceError = new ServiceError("Company OID cannot be null");
            List<ServiceError> errors = Lists.newArrayList(serviceError);
            throw new ServiceException(errors);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Long save(CompanyDTO companyDTO) {
        this.logger.info(StringUtils.concat("Saving company - firm: ", companyDTO.getFirm(), " - name: ", companyDTO
            .getName(), " - address: ", companyDTO.getAddress(), " - description: ", companyDTO.getDescription(),
            " - websiteURL: ", companyDTO.getWebsiteURL(), " - pictureName: ", companyDTO.getPicture() == null
                ? "no picture" : companyDTO.getPicture().getFileName(), " contact list size: ", companyDTO.getContacts()
                .size()));

        this.validateCompanyDTO(companyDTO);

        if (companyDTO.getOID() == null) {
            companyDTO.setCreationDate(new Date().getTime());
        }

        Company company = this.companyTranslator.getPersistentObject(companyDTO);

        this.saveCompany(company);

        this.logger.info(StringUtils.concat("Company saved with OID ", company.getOID()));

        return company.getOID();
    }


    private void validateCompanyDTO(CompanyDTO companyDTO) {
        List<ServiceError> errors = Lists.newArrayList();
        PictureDTO picture = companyDTO.getPicture();
        String firm = companyDTO.getFirm();
        String name = companyDTO.getName();

        if (firm == null || firm.isEmpty()) {
            errors.add(new ServiceError("Firm cannot be null or empty"));
        }
        if (name == null || name.isEmpty()) {
            errors.add(new ServiceError("Name cannot be null or empty"));
        }
        if (picture == null || picture.getFileName().isEmpty()) {
            errors.add(new ServiceError("Picture name cannot be null or empty"));
        }

        if (!errors.isEmpty()) {
            throw new ServiceException(errors);
        }
    }

    public CompanySearchResultContainerDTO searchCompaniesByCriteria(final CompanyFilterDTO companyFilterDTO) {
        this.logger
            .info(StringUtils.concat("Getting companies - searchCriteria: ", companyFilterDTO.getSearchCriteria(),
                " - pageSize: ", companyFilterDTO.getPageSize(), " - pageNumber: ", companyFilterDTO.getPageNumber(),
                " - orderByField: ", companyFilterDTO.getOrderBy(), " - orderDirection: ", companyFilterDTO
                    .getOrderDirection()));

        CompanySearchResultContainerDTO companySearchResultContainerDTO = new CompanySearchResultContainerDTO();
        List<Object[]> companiesByCriteria = CompanyServiceImpl.this.companyDAO.searchCompaniesByCriteria(companyFilterDTO
            .getSearchCriteria(), companyFilterDTO.getPageSize(), companyFilterDTO.getPageNumber(),
            CompanyServiceImpl.this.mapCompanySortingValues.get(companyFilterDTO.getOrderBy().name()),
            CompanyServiceImpl.this.getOrder(companyFilterDTO.getOrderDirection()));
        List<CompanySearchResultDTO> companies = CompanyServiceImpl.this.companyByCriteriaTranslator
            .getCompanySearchResultList(companiesByCriteria);
        Integer numberOfResults = CompanyServiceImpl.this.companyDAO.countCompaniesByCriteria(companyFilterDTO
            .getSearchCriteria());
        companySearchResultContainerDTO.setCompanies(companies);
        companySearchResultContainerDTO.setNumberOfResults(numberOfResults);

        this.logger.info(StringUtils.concat("Returning ", companySearchResultContainerDTO.getNumberOfResults(), " results"));

        return companySearchResultContainerDTO;

    }

    private OrderType getOrder(OrderDirectionType orderDirectionType) {
        if (orderDirectionType.name().equals("ASC")) {
            return OrderType.ASC;
        }
        if (orderDirectionType.name().equals("DESC")) {
            return OrderType.DES;
        }
        this.logger.warn("Se esta llegando un orden que no es ni desc ni asc en la busqueda de beneficio");
        return OrderType.ASC;
    }

    public List<CompanyIdentifierDTO> getAllCompanies() {
        List<Company> companies = CompanyServiceImpl.this.companyDAO.getAllCompanies();
        List<CompanyIdentifierDTO> companyIdentifierDTOs = new ArrayList<CompanyIdentifierDTO>();
        for (Company company : companies) {
            companyIdentifierDTOs.add(this.companyIdentifierTranslator.getDTO(company));
        }
        return companyIdentifierDTOs;
    }

    @Override
    public void deleteCompany(Long companyOID) {
        this.logger.info(StringUtils.concat("Deleting company with OID ", companyOID));

        this.validateCompanyOID(companyOID);

        Company company = this.companyDAO.read(companyOID);

        this.validateCompanyNotNull(company);

        this.deleteCompany(company);
        this.logger.info(StringUtils.concat("Company woth OID ", companyOID, " has been deleted"));
    }

    private void deleteCompany(Company company) {
        this.companyDAO.deleteWithFlush(company);
        List<Long> list = this.benefitDAO.getApplianceOIDsByCompanyOID(company.getOID());
        if (list != null) {
            this.benefitIndexManager.delete(list);
        }
    }

    private void saveCompany(Company company) {
        this.companyDAO.saveWithFlush(company);
        List<Long> list = this.benefitDAO.getBenefitOIDsByCompanyOID(company.getOID());
        if (list != null) {
            this.benefitIndexManager.update(list);
        }
    }

    private void validateCompanyNotNull(Company company) {
        if (company == null) {
            ServiceError serviceError = new ServiceError("No company found");
            List<ServiceError> errors = Lists.newArrayList(serviceError);
            throw new ServiceException(errors);
        }
    }

    // Setters for dependency injection
    public void setCompanyDAO(CompanyDAO companyDAO) {
        this.companyDAO = companyDAO;
    }

    public void setCompanyTranslator(CompanyTranslator companyTranslator) {
        this.companyTranslator = companyTranslator;
    }

    public void setCompanyByCriteriaTranslator(CompanyByCriteriaTranslator companyByCriteriaTranslator) {
        this.companyByCriteriaTranslator = companyByCriteriaTranslator;
    }

    public void setCompanyIdentifierTranslator(CompanyIdentifierTranslator companyIdentifierTranslator) {
        this.companyIdentifierTranslator = companyIdentifierTranslator;
    }

    public void setMapCompanySortingValues(Map<String, String> mapCompanySortingValues) {
        this.mapCompanySortingValues = mapCompanySortingValues;
    }

    public void setBenefitIndexManager(BenefitIndexManager benefitIndexManager) {
        this.benefitIndexManager = benefitIndexManager;
    }

    public void setBenefitDAO(BenefitDAO benefitDAO) {
        this.benefitDAO = benefitDAO;
    }
}
