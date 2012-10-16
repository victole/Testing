package com.despegar.sobek.translator;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.despegar.framework.mapper.translator.AbstractIdentifiableDTOTranslator;
import com.despegar.library.rest.connector.exceptions.ServiceError;
import com.despegar.library.rest.connector.exceptions.ServiceException;
import com.despegar.framework.utils.string.StringUtils;
import com.despegar.geodespegar.dto.GeoAreaDescriptionDTO;
import com.despegar.sobek.dao.BenefitCategoryDAO;
import com.despegar.sobek.dao.BenefitStatusDAO;
import com.despegar.sobek.dao.BrandDAO;
import com.despegar.sobek.dao.CompanyDAO;
import com.despegar.sobek.dao.GeoAreaDAO;
import com.despegar.sobek.dao.LanguageDAO;
import com.despegar.sobek.dao.ProductDAO;
import com.despegar.sobek.dto.BenefitApplianceDTO;
import com.despegar.sobek.dto.BenefitDTO;
import com.despegar.sobek.dto.BenefitInformationDTO;
import com.despegar.sobek.exception.ServiceErrorCode;
import com.despegar.sobek.model.Appliance;
import com.despegar.sobek.model.Benefit;
import com.despegar.sobek.model.BenefitCategory;
import com.despegar.sobek.model.BenefitDescriptionI18N;
import com.despegar.sobek.model.BenefitStatus;
import com.despegar.sobek.model.Brand;
import com.despegar.sobek.model.Company;
import com.despegar.sobek.model.GeoArea;
import com.despegar.sobek.model.Language;
import com.despegar.sobek.model.Product;
import com.despegar.sobek.model.VoucherI18N;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;

public class BenefitTranslator
    extends AbstractIdentifiableDTOTranslator<BenefitDTO, Benefit> {

    private LanguageDAO languageDAO;
    private BenefitCategoryDAO benefitCategoryDAO;
    private BenefitStatusDAO benefitStatusDAO;
    private BrandDAO brandDAO;
    private ProductDAO productDAO;
    private CompanyDAO companyDAO;
    private GeoAreaDAO geoAreaDAO;
    private PictureTranslator pictureTranslator;

    @Override
    public Benefit fillPersistentObject(BenefitDTO dto, Benefit entity) {

        BenefitCategory benefitCategory = this.translateBenefitCategoryDTO(dto.getCategoryCode());

        Set<VoucherI18N> vouchersI18N = this.translateVouchers(dto.getExternalResources());
        Set<BenefitDescriptionI18N> benefitDescriptionI18N = this
            .translateBenefitInformationDTO(dto.getBenefitInformation());
        Set<Appliance> appliances = this.translateAppliances(dto.getAppliances());

        entity.getAppliance().clear();
        entity.getBenefitDescriptionI18N().clear();
        entity.getExternalResource().clear();

        entity.setBenefitCategory(benefitCategory);
        entity.setCompany(this.companyDAO.getCompanyByOID(dto.getCompanyOID()));
        entity.setDateFrom(new Date(dto.getDateFrom()));
        entity.setDateTo(new Date(dto.getDateTo()));
        entity.setIsFree(dto.getIsFree());
        entity.setIsOutstanding(dto.getIsOutstanding());
        this.fillBenefitStatus(dto, entity);

        for (BenefitDescriptionI18N description : benefitDescriptionI18N) {
            entity.addBenefitDescriptionI18N(description);

        }
        for (VoucherI18N voucherI18N : vouchersI18N) {
            entity.addExternalResource(voucherI18N);
        }

        for (Appliance appliance : appliances) {
            entity.addAppliance(appliance);
        }

        return entity;
    }

    private Set<Appliance> translateAppliances(Set<BenefitApplianceDTO> appliancesDTO) {
        Appliance appliance = null;
        Brand brand = null;
        Product product = null;
        GeoArea originGeoArea = null;
        GeoArea destinationGeoArea = null;
        Set<Appliance> appliances = new HashSet<Appliance>();

        for (BenefitApplianceDTO benefitApplianceDTO : appliancesDTO) {
            appliance = new Appliance();

            brand = this.getBrandByCode(benefitApplianceDTO.getBrandCode());

            product = this.getProductByCode(benefitApplianceDTO.getProductCode());

            originGeoArea = this.getGeoAreaByItemOID(benefitApplianceDTO.getOriginOID());

            destinationGeoArea = this.getGeoAreaByItemOID(benefitApplianceDTO.getDestinationOID());

            appliance.setBrand(brand);
            appliance.setProduct(product);
            appliance.setDestinationGeoArea(destinationGeoArea);
            appliance.setOriginGeoArea(originGeoArea);

            appliances.add(appliance);
        }

        return appliances;
    }

    private Brand getBrandByCode(String brandCode) {
        Brand brand = this.brandDAO.findByCode(brandCode);
        if (brand == null && brandCode != null) {
            ServiceError serviceError = new ServiceError(ServiceErrorCode.BENEFIT_UNKNOWN_BRAND.getCode(),
                StringUtils.concat("Brand ", brandCode, "is unknown"));
            throw new ServiceException(Lists.newArrayList(serviceError));
        }
        return brand;
    }

    private Product getProductByCode(String productCode) {
        Product product = this.productDAO.findByCode(productCode);
        if (product == null && productCode != null) {
            ServiceError serviceError = new ServiceError(ServiceErrorCode.BENEFIT_UNKNOWN_PRODUCT.getCode(),
                StringUtils.concat("Product ", productCode, "is unknown"));
            throw new ServiceException(Lists.newArrayList(serviceError));
        }
        return product;
    }

    /**
     * Get a GeoArea or creates it
     * 
     * @param benefitApplianceDTO
     * @param itemOID
     * @param cachedGeoAreas
     */
    private GeoArea getGeoAreaByItemOID(Long itemOID) {
        GeoArea geoArea = this.geoAreaDAO.getGeoAreaByItemOID(itemOID);
        if (geoArea == null && itemOID != null) {
            ServiceError serviceError = new ServiceError(ServiceErrorCode.BENEFIT_UNKNOWN_GEOAREA.getCode(),
                StringUtils.concat("GeoArea itemOID: ", itemOID, "is unknown"));
            throw new ServiceException(Lists.newArrayList(serviceError));
        }
        return geoArea;
    }

    private Set<VoucherI18N> translateVouchers(Map<String, String> voucher) {
        Set<VoucherI18N> vouchers = new HashSet<VoucherI18N>();
        Language language = null;
        VoucherI18N voucherI18N = null;
        for (String isoCode : voucher.keySet()) {
            if (voucher.get(isoCode) != null) {
                language = this.languageDAO.findByIsoCode(isoCode);
                voucherI18N = new VoucherI18N();
                voucherI18N.setLanguage(language);
                voucherI18N.setResourceName(voucher.get(isoCode));
                voucherI18N.setIsGenerated(false);
                vouchers.add(voucherI18N);
            }
        }

        return vouchers;
    }

    private BenefitStatus translateBenefitStatusDTO(String statusCode) {
        BenefitStatus benefitStatus = this.benefitStatusDAO.findByCode(statusCode);
        if (benefitStatus == null) {
            ServiceError serviceError = new ServiceError(ServiceErrorCode.BENEFIT_UNKNOWN_STATUS.getCode(),
                StringUtils.concat("Benefit Status ", statusCode, "is unknown"));
            throw new ServiceException(Lists.newArrayList(serviceError));
        }
        return benefitStatus;
    }

    private BenefitCategory translateBenefitCategoryDTO(String categoryCode) {
        BenefitCategory benefitCategory = this.benefitCategoryDAO.findByCode(categoryCode);
        if (benefitCategory == null) {
            ServiceError serviceError = new ServiceError(ServiceErrorCode.BENEFIT_UNKNOWN_CATEGORY.getCode(),
                StringUtils.concat("Benefit Category ", categoryCode, "is unknown"));
            throw new ServiceException(Lists.newArrayList(serviceError));
        }
        return benefitCategory;
    }

    private Set<BenefitDescriptionI18N> translateBenefitInformationDTO(Map<String, BenefitInformationDTO> benefitInformation) {
        Set<BenefitDescriptionI18N> benefitDescriptionI18N = new HashSet<BenefitDescriptionI18N>();

        BenefitInformationDTO benefitInformationDTO = null;
        for (String languageIsoCode : benefitInformation.keySet()) {
            benefitInformationDTO = benefitInformation.get(languageIsoCode);
            Language language = this.languageDAO.findByIsoCode(languageIsoCode);
            if (language == null) {
                ServiceError serviceError = new ServiceError(ServiceErrorCode.BENEFIT_UNKNOWN_LANGUAGE.getCode(),
                    StringUtils.concat("Language ", languageIsoCode, "is unknown"));
                throw new ServiceException(Lists.newArrayList(serviceError));
            }

            BenefitDescriptionI18N benefitDescription = new BenefitDescriptionI18N();
            benefitDescription.setBranches(benefitInformationDTO.getBranches());
            benefitDescription.setDescription(benefitInformationDTO.getDescription());
            benefitDescription.setLanguage(language);
            benefitDescription.setOID(benefitInformationDTO.getOID());
            benefitDescription.setTermsAndConditions(benefitInformationDTO.getTermsAndConditions());
            benefitDescription.setTitle(benefitInformationDTO.getTitle());
            benefitDescription.setLink(benefitInformationDTO.getLink());
            benefitDescription.setLinkTitle(benefitInformationDTO.getLinkTitle());
            benefitDescription.setVersion(benefitInformationDTO.getVersion());
            benefitDescriptionI18N.add(benefitDescription);
        }
        return benefitDescriptionI18N;
    }

    @Override
    protected BenefitDTO fillDTO(BenefitDTO dto, Benefit benefit, Object context) {

        Company company = benefit.getCompany();
        dto.setAppliances(this.translateAppliance(benefit.getAppliance(), context));
        dto.setBenefitInformation(this.translateBenefitDescriptionI18N(benefit.getBenefitDescriptionI18N()));
        dto.setBenefitStatusCode(benefit.getBenefitStatus().getCode());
        dto.setCategoryCode(benefit.getBenefitCategory().getCode());
        dto.setCompanyOID(company.getOID());
        dto.setDateFrom(benefit.getDateFrom().getTime());
        dto.setDateTo(benefit.getDateTo().getTime());
        dto.setExternalResources(this.translateVoucherI18N(benefit.getExternalResource()));
        dto.setIsFree(benefit.getIsFree());
        dto.setIsOutstanding(benefit.getIsOutstanding());
        dto.setPicture(this.pictureTranslator.getDTO(company.getPicture()));
        dto.setRelevance(benefit.getRelevance());

        return dto;
    }

    private Map<String, String> translateVoucherI18N(Set<VoucherI18N> voucherI18Ns) {
        Map<String, String> vouchers = new HashMap<String, String>();

        for (VoucherI18N voucherI18N : voucherI18Ns) {
            vouchers.put(voucherI18N.getLanguage().getIsoCode(), voucherI18N.getResourceName());
        }
        return vouchers;
    }

    private Map<String, BenefitInformationDTO> translateBenefitDescriptionI18N(
        Set<BenefitDescriptionI18N> benefitDescriptionI18N) {

        Map<String, BenefitInformationDTO> benefitInformationDTOs = new HashMap<String, BenefitInformationDTO>();

        BenefitInformationDTO benefitInformationDTO = null;
        for (BenefitDescriptionI18N description : benefitDescriptionI18N) {
            benefitInformationDTO = new BenefitInformationDTO();
            benefitInformationDTO.setBranches(description.getBranches());
            benefitInformationDTO.setDescription(description.getDescription());
            benefitInformationDTO.setLink(description.getLink());
            benefitInformationDTO.setLinkTitle(description.getLinkTitle());
            benefitInformationDTO.setOID(description.getOID());
            benefitInformationDTO.setTermsAndConditions(description.getTermsAndConditions());
            benefitInformationDTO.setTitle(description.getTitle());
            benefitInformationDTOs.put(description.getLanguage().getIsoCode(), benefitInformationDTO);
        }

        return benefitInformationDTOs;
    }

    private Set<BenefitApplianceDTO> translateAppliance(Set<Appliance> appliances, Object context) {
        @SuppressWarnings("unchecked")
        ListMultimap<Long, GeoAreaDescriptionDTO> geoAreasDescriptions = (ListMultimap<Long, GeoAreaDescriptionDTO>) context;

        Set<BenefitApplianceDTO> applianceDTOs = new HashSet<BenefitApplianceDTO>();

        BenefitApplianceDTO applianceDTO = null;
        Brand brand = null;
        GeoArea destinationGeoArea = null;
        GeoArea originGeoArea = null;
        Product product = null;
        Long itemOID = null;

        for (Appliance appliance : appliances) {
            applianceDTO = new BenefitApplianceDTO();
            brand = appliance.getBrand();
            destinationGeoArea = appliance.getDestinationGeoArea();
            originGeoArea = appliance.getOriginGeoArea();
            product = appliance.getProduct();

            if (destinationGeoArea != null) {
                itemOID = destinationGeoArea.getDespegarItemOID();
                applianceDTO.setDestinationOID(itemOID);
                applianceDTO.setDestinationType(destinationGeoArea.getType());
                applianceDTO.setDestinationDescriptions(this.transformGeoAreaDescriptions(geoAreasDescriptions, itemOID));
            }

            if (originGeoArea != null) {
                itemOID = originGeoArea.getDespegarItemOID();
                applianceDTO.setOriginOID(itemOID);
                applianceDTO.setOriginType(originGeoArea.getType());
                applianceDTO.setOriginDescriptions(this.transformGeoAreaDescriptions(geoAreasDescriptions, itemOID));
            }

            applianceDTO.setBrandCode((brand == null) ? null : brand.getCode());
            applianceDTO.setProductCode((product == null) ? null : product.getCode());

            applianceDTOs.add(applianceDTO);
        }

        return applianceDTOs;
    }

    private Map<String, String> transformGeoAreaDescriptions(ListMultimap<Long, GeoAreaDescriptionDTO> geoAreasDescriptions,
        Long despegarItemOID) {
        List<GeoAreaDescriptionDTO> list = geoAreasDescriptions.get(despegarItemOID);
        Map<String, String> description = new HashMap<String, String>();

        for (GeoAreaDescriptionDTO descDTO : list) {
            description.put(descDTO.getLanguageCode(), descDTO.getName());
        }
        return description;
    }

    public Benefit fillBenefitStatus(BenefitDTO benefitDTO, Benefit benefit) {
        BenefitStatus benefitStatus = this.translateBenefitStatusDTO(benefitDTO.getBenefitStatusCode());
        benefit.setBenefitStatus(benefitStatus);
        return benefit;
    }

    public void setBenefitCategoryDAO(BenefitCategoryDAO benefitCategoryDAO) {
        this.benefitCategoryDAO = benefitCategoryDAO;
    }

    public void setBenefitStatusDAO(BenefitStatusDAO benefitStatusDAO) {
        this.benefitStatusDAO = benefitStatusDAO;
    }

    public void setLanguageDAO(LanguageDAO languageDAO) {
        this.languageDAO = languageDAO;
    }

    public void setBrandDAO(BrandDAO brandDAO) {
        this.brandDAO = brandDAO;
    }

    public void setProductDAO(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public void setCompanyDAO(CompanyDAO companyDAO) {
        this.companyDAO = companyDAO;
    }

    public void setGeoAreaDAO(GeoAreaDAO geoAreaDAO) {
        this.geoAreaDAO = geoAreaDAO;
    }

    public void setPictureTranslator(PictureTranslator pictureTranslator) {
        this.pictureTranslator = pictureTranslator;
    }

}
