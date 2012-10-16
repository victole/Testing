package com.despegar.sobek.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import com.despegar.framework.persistence.hibernate.dao.ReadWriteObjectDAO;
import com.despegar.framework.tests.spring.AbstractTransactionalSpringTest;
import com.despegar.sobek.dao.CompanyDAO;
import com.despegar.sobek.dto.BenefitApplianceDTO;
import com.despegar.sobek.dto.BenefitDTO;
import com.despegar.sobek.dto.BenefitInformationDTO;
import com.despegar.sobek.model.BenefitCategory;
import com.despegar.sobek.model.BenefitStatus;
import com.despegar.sobek.model.BenefitStatusCode;
import com.despegar.sobek.model.Brand;
import com.despegar.sobek.model.Company;
import com.despegar.sobek.model.GeoArea;
import com.despegar.sobek.model.Language;
import com.despegar.sobek.model.Product;
import com.despegar.sobek.solr.index.manager.BenefitIndexManager;
import com.despegar.sobek.utility.ModelContentCreator;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@ContextConfiguration(locations = "classpath:com/despegar/test/test-reference-data-context.xml")
@Ignore
public class BenefitServiceIntegrationTest
    extends AbstractTransactionalSpringTest {

    @Autowired
    private BenefitServiceImpl benefitService;

    @Autowired
    private CompanyDAO companyDAO;
    @Autowired
    private ReadWriteObjectDAO<Language> languageReadWriteDAO;
    @Autowired
    private ReadWriteObjectDAO<BenefitCategory> benefitCategoryReadWriteDAO;
    @Autowired
    private ReadWriteObjectDAO<BenefitStatus> benefitStatusReadWriteDAO;
    @Autowired
    private ReadWriteObjectDAO<Brand> brandReadWriteDAO;
    @Autowired
    private ReadWriteObjectDAO<Product> productReadWriteDAO;
    @Autowired
    private ReadWriteObjectDAO<GeoArea> geoAreaReadWriteDAO;

    @Mock
    private BenefitIndexManager benefitIndexManager;

    private Brand brand;

    private Product product;

    private GeoArea geoArea;

    private BenefitCategory category;

    private BenefitStatus status;

    private Language language;

    private Company company;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        this.company = ModelContentCreator.createCompany();
        this.companyDAO.save(this.company);

        this.language = ModelContentCreator.createLanguage();
        this.languageReadWriteDAO.save(this.language);

        this.category = ModelContentCreator.createBenefitCategory(this.language);
        this.benefitCategoryReadWriteDAO.save(this.category);

        this.status = ModelContentCreator.createBenefitStatus(BenefitStatusCode.PUBLISHED, this.language);
        this.benefitStatusReadWriteDAO.save(this.status);

        this.geoArea = ModelContentCreator.createGeoArea();
        this.geoAreaReadWriteDAO.save(this.geoArea);

        this.brand = ModelContentCreator.createBrand(this.language);
        this.brandReadWriteDAO.save(this.brand);

        this.product = ModelContentCreator.createProduct(this.language);
        this.productReadWriteDAO.save(this.product);

        this.benefitService.setBenefitIndexManager(this.benefitIndexManager);
        Mockito.doNothing().when(this.benefitIndexManager).update(Mockito.anyListOf(Long.class));
        Mockito.doNothing().when(this.benefitIndexManager).delete(Mockito.anyListOf(Long.class));

    }

    @Test
    public void saveBenefit_nullBrandCode_returnBenefitOID() {

        GeoArea destinationGeoArea = ModelContentCreator.createGeoArea();
        this.geoAreaReadWriteDAO.save(destinationGeoArea);

        BenefitApplianceDTO applianceDTO = new BenefitApplianceDTO();
        applianceDTO.setBrandCode(null);
        applianceDTO.setProductCode(this.product.getCode());
        applianceDTO.setOriginOID(this.geoArea.getDespegarItemOID());
        applianceDTO.setOriginType(this.geoArea.getType());
        applianceDTO.setDestinationOID(destinationGeoArea.getDespegarItemOID());
        applianceDTO.setDestinationType(destinationGeoArea.getType());

        Map<String, BenefitInformationDTO> informationDTOs = this.createBenefitInformationByLanguage();
        BenefitDTO dto = this.createBenefitDTO(applianceDTO, informationDTOs);

        Long benefitOID = this.benefitService.save(dto);

        assertNotNull(benefitOID);
    }

    @Test
    public void saveBenefit_nullProductCode_returnBenefitOID() {

        GeoArea destinationGeoArea = ModelContentCreator.createGeoArea();
        this.geoAreaReadWriteDAO.save(destinationGeoArea);

        BenefitApplianceDTO applianceDTO = new BenefitApplianceDTO();
        applianceDTO.setBrandCode(this.brand.getCode());
        applianceDTO.setProductCode(null);
        applianceDTO.setOriginOID(this.geoArea.getDespegarItemOID());
        applianceDTO.setOriginType(this.geoArea.getType());
        applianceDTO.setDestinationOID(destinationGeoArea.getDespegarItemOID());
        applianceDTO.setDestinationType(destinationGeoArea.getType());

        Map<String, BenefitInformationDTO> informationDTOs = this.createBenefitInformationByLanguage();
        BenefitDTO dto = this.createBenefitDTO(applianceDTO, informationDTOs);

        Long benefitOID = this.benefitService.save(dto);

        assertNotNull(benefitOID);
    }

    @Test
    public void saveBenefit_nullGeoArea_returnBenefitOID() {

        GeoArea destinationGeoArea = ModelContentCreator.createGeoArea();
        this.geoAreaReadWriteDAO.save(destinationGeoArea);

        BenefitApplianceDTO applianceDTO = new BenefitApplianceDTO();
        applianceDTO.setBrandCode(this.brand.getCode());
        applianceDTO.setProductCode(this.product.getCode());
        applianceDTO.setOriginOID(null);
        applianceDTO.setOriginType(null);
        applianceDTO.setDestinationOID(destinationGeoArea.getDespegarItemOID());
        applianceDTO.setDestinationType(destinationGeoArea.getType());

        Map<String, BenefitInformationDTO> informationDTOs = this.createBenefitInformationByLanguage();
        BenefitDTO dto = this.createBenefitDTO(applianceDTO, informationDTOs);

        Long benefitOID = this.benefitService.save(dto);

        assertNotNull(benefitOID);
    }

    @Test
    public void saveBenefit_existentGeoArea_returnBenefitOID() {

        GeoArea destinationGeoArea = ModelContentCreator.createGeoArea();
        this.geoAreaReadWriteDAO.save(destinationGeoArea);

        BenefitApplianceDTO applianceDTO = new BenefitApplianceDTO();
        applianceDTO.setBrandCode(this.brand.getCode());
        applianceDTO.setProductCode(this.product.getCode());
        applianceDTO.setOriginOID(this.geoArea.getDespegarItemOID());
        applianceDTO.setOriginType(this.geoArea.getType());
        applianceDTO.setDestinationOID(destinationGeoArea.getDespegarItemOID());
        applianceDTO.setDestinationType(destinationGeoArea.getType());

        Map<String, BenefitInformationDTO> informationDTOs = this.createBenefitInformationByLanguage();
        BenefitDTO dto = this.createBenefitDTO(applianceDTO, informationDTOs);

        Long benefitOID = this.benefitService.save(dto);

        assertNotNull(benefitOID);
    }

    @Test
    public void saveBenefit_publishBrandNewBenefit_returnBenefit() {
        GeoArea destinationGeoArea = ModelContentCreator.createGeoArea();
        this.geoAreaReadWriteDAO.save(destinationGeoArea);

        BenefitApplianceDTO applianceDTO = new BenefitApplianceDTO();
        applianceDTO.setBrandCode(this.brand.getCode());
        applianceDTO.setProductCode(this.product.getCode());
        applianceDTO.setOriginOID(this.geoArea.getDespegarItemOID());
        applianceDTO.setOriginType(this.geoArea.getType());
        applianceDTO.setDestinationOID(destinationGeoArea.getDespegarItemOID());
        applianceDTO.setDestinationType(destinationGeoArea.getType());

        Map<String, BenefitInformationDTO> informationDTOs = this.createBenefitInformationByLanguage();
        BenefitDTO dto = this.createBenefitDTO(applianceDTO, informationDTOs);
        dto.setBenefitStatusCode(BenefitStatusCode.PUBLISHED.getCode());

        Long benefitOID = this.benefitService.save(dto);

        assertNotNull(benefitOID);

        BenefitDTO benefit = this.benefitService.getBenefit(benefitOID);
        assertNotNull(benefit);
        assertEquals(1, benefit.getExternalResources().size());
    }

    @Test
    public void saveBenefit_publishUnplublishedBenefit_returnBenefit() {
        GeoArea destinationGeoArea = ModelContentCreator.createGeoArea();
        this.geoAreaReadWriteDAO.save(destinationGeoArea);

        BenefitApplianceDTO applianceDTO = new BenefitApplianceDTO();
        applianceDTO.setBrandCode(this.brand.getCode());
        applianceDTO.setProductCode(this.product.getCode());
        applianceDTO.setOriginOID(this.geoArea.getDespegarItemOID());
        applianceDTO.setOriginType(this.geoArea.getType());
        applianceDTO.setDestinationOID(destinationGeoArea.getDespegarItemOID());
        applianceDTO.setDestinationType(destinationGeoArea.getType());

        Map<String, BenefitInformationDTO> informationDTOs = this.createBenefitInformationByLanguage();
        BenefitDTO dto = this.createBenefitDTO(applianceDTO, informationDTOs);
        dto.setBenefitStatusCode(BenefitStatusCode.UNPUBLISHED.getCode());

        Long benefitOID = this.benefitService.save(dto);

        assertNotNull(benefitOID);

        dto.setOID(benefitOID);
        dto.setBenefitStatusCode(BenefitStatusCode.PUBLISHED.getCode());

        benefitOID = this.benefitService.save(dto);

        BenefitDTO benefit = this.benefitService.getBenefit(benefitOID);

        assertNotNull(benefit);
        assertEquals(1, benefit.getExternalResources().size());
    }

    private Map<String, BenefitInformationDTO> createBenefitInformationByLanguage() {
        Map<String, BenefitInformationDTO> informationDTOs = Maps.newHashMap();
        BenefitInformationDTO value = new BenefitInformationDTO();
        value.setTitle("Information");
        value.setLink("www.information.com");
        value.setLinkTitle("Info");
        informationDTOs.put(this.language.getIsoCode(), value);
        return informationDTOs;
    }

    private BenefitDTO createBenefitDTO(BenefitApplianceDTO applianceDTO, Map<String, BenefitInformationDTO> informationDTOs) {
        BenefitDTO dto = new BenefitDTO();
        dto.setAppliances(Sets.newHashSet(applianceDTO));
        dto.setBenefitStatusCode(this.status.getCode());
        dto.setCategoryCode(this.category.getCode());
        dto.setCompanyOID(this.company.getOID());
        dto.setDateFrom(System.currentTimeMillis());
        dto.setDateTo(System.currentTimeMillis());
        dto.setIsFree(true);
        dto.setIsOutstanding(true);
        dto.setBenefitInformation(informationDTOs);
        dto.setExternalResources(new HashMap<String, String>());
        return dto;
    }
}
