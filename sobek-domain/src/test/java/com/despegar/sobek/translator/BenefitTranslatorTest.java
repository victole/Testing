package com.despegar.sobek.translator;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.despegar.framework.persistence.hibernate.dao.generic.GenericReadWriteObjectDAO;
import com.despegar.geodespegar.dto.GeoAreaDescriptionDTO;
import com.despegar.library.rest.connector.exceptions.ServiceException;
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
import com.despegar.sobek.model.Appliance;
import com.despegar.sobek.model.Benefit;
import com.despegar.sobek.model.BenefitCategory;
import com.despegar.sobek.model.BenefitDescriptionI18N;
import com.despegar.sobek.model.BenefitStatus;
import com.despegar.sobek.model.BenefitStatusCode;
import com.despegar.sobek.model.Brand;
import com.despegar.sobek.model.Company;
import com.despegar.sobek.model.GeoArea;
import com.despegar.sobek.model.Language;
import com.despegar.sobek.model.Product;
import com.despegar.sobek.model.VoucherI18N;
import com.despegar.sobek.utility.ModelContentCreator;
import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;

public class BenefitTranslatorTest {

    @Mock
    private LanguageDAO languageDAOMock;
    @Mock
    private BenefitCategoryDAO benefitCategoryDAOMock;
    @Mock
    private BenefitStatusDAO benefitStatusDAOMock;
    @Mock
    private BrandDAO brandDAOMock;
    @Mock
    private ProductDAO productDAOMock;
    @Mock
    private GenericReadWriteObjectDAO<Benefit> benefitDAOMock;
    @Mock
    private CompanyDAO companyDAOMock;
    @Mock
    private GeoAreaDAO geoAreaMock;
    @Mock
    private PictureTranslator pictureTranslatorMock;

    private BenefitTranslator instance;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.instance = new BenefitTranslator();
        this.instance.setBenefitCategoryDAO(this.benefitCategoryDAOMock);
        this.instance.setBenefitStatusDAO(this.benefitStatusDAOMock);
        this.instance.setEntityDAO(this.benefitDAOMock);
        this.instance.setLanguageDAO(this.languageDAOMock);
        this.instance.setBrandDAO(this.brandDAOMock);
        this.instance.setProductDAO(this.productDAOMock);
        this.instance.setCompanyDAO(this.companyDAOMock);
        this.instance.setGeoAreaDAO(this.geoAreaMock);
        this.instance.setPictureTranslator(this.pictureTranslatorMock);

        Language languageEs = new Language();
        languageEs.setIsoCode("ES");
        languageEs.setName("Español");

        Language languageBr = new Language();
        languageBr.setIsoCode("BR");
        languageBr.setName("Portugues");

        BenefitCategory category = new BenefitCategory();
        category.setCode("CAT");

        BenefitStatus status = new BenefitStatus();
        status.setCode(BenefitStatusCode.PUBLISHED.getCode());

        Brand brand = new Brand();
        brand.setCode("DESP");

        Product product = new Product();
        product.setCode("PROD");

        Company company = new Company();
        company.setOID(123L);

        GeoArea destinationGeoArea = new GeoArea();
        destinationGeoArea.setDespegarItemOID(123L);
        destinationGeoArea.setType("T");

        GeoArea originGeoArea = new GeoArea();
        originGeoArea.setDespegarItemOID(234L);
        originGeoArea.setType("T");

        when(this.benefitCategoryDAOMock.findByCode("CAT")).thenReturn(category);
        when(this.benefitStatusDAOMock.findByCode(BenefitStatusCode.PUBLISHED.getCode())).thenReturn(status);
        when(this.brandDAOMock.findByCode("DESP")).thenReturn(brand);
        when(this.productDAOMock.findByCode("PROD")).thenReturn(product);
        when(this.languageDAOMock.findByIsoCode("ES")).thenReturn(languageEs);
        when(this.languageDAOMock.findByIsoCode("BR")).thenReturn(languageBr);
        when(this.companyDAOMock.getCompanyByOID(123L)).thenReturn(company);
        when(this.geoAreaMock.getGeoAreaByItemOID(123L)).thenReturn(destinationGeoArea);
        when(this.geoAreaMock.getGeoAreaByItemOID(234L)).thenReturn(originGeoArea);
    }

    @Test
    public void getPersistentObject_emptyLists_returnsBenefit() {
        BenefitDTO dto = this.createBenefitDTO();

        Benefit benefit = this.instance.getPersistentObject(dto);

        this.assertBenefit(dto, benefit);

    }

    @Test
    public void getPersistentObject_appliance_returnsBenefit() {
        BenefitDTO dto = this.createBenefitDTO();

        BenefitApplianceDTO applianceDTO = new BenefitApplianceDTO();
        applianceDTO.setBrandCode("DESP");
        applianceDTO.setDestinationOID(123L);
        applianceDTO.setDestinationType("T");
        applianceDTO.setOriginOID(234L);
        applianceDTO.setOriginType("T");
        applianceDTO.setProductCode("PROD");

        Set<BenefitApplianceDTO> appliances = dto.getAppliances();
        appliances.add(applianceDTO);

        Benefit benefit = this.instance.getPersistentObject(dto);

        this.assertBenefit(dto, benefit);
    }

    @Test
    public void getPersistentObject_existentGeoArea_returnsBenefit() {
        BenefitDTO dto = this.createBenefitDTO();

        BenefitApplianceDTO applianceDTO = new BenefitApplianceDTO();
        applianceDTO.setBrandCode("DESP");
        applianceDTO.setDestinationOID(123L);
        applianceDTO.setDestinationType("T");
        applianceDTO.setOriginOID(234L);
        applianceDTO.setOriginType("T");
        applianceDTO.setProductCode("PROD");

        Set<BenefitApplianceDTO> appliances = dto.getAppliances();
        appliances.add(applianceDTO);

        GeoArea geoArea = new GeoArea();
        geoArea.setDespegarItemOID(123L);
        geoArea.setType("T");
        when(this.geoAreaMock.read(123L)).thenReturn(geoArea);

        Benefit benefit = this.instance.getPersistentObject(dto);

        this.assertBenefit(dto, benefit);
    }

    @Test
    public void getPersistentObject_voucher_returnsBenefit() {
        BenefitDTO dto = this.createBenefitDTO();

        Map<String, String> voucherDTO = Maps.newHashMap();
        voucherDTO.put("ES", "voucher_ES.pdf");
        voucherDTO.put("BR", "voucher_BR.pdf");

        dto.getExternalResources().putAll(voucherDTO);

        Benefit benefit = this.instance.getPersistentObject(dto);

        this.assertBenefit(dto, benefit);
    }

    @Test
    public void getPersistentObject_benefitInformation_returnsBenefit() {
        BenefitDTO dto = this.createBenefitDTO();
        BenefitInformationDTO informationDTO = new BenefitInformationDTO();
        informationDTO.setBranches("branch");
        informationDTO.setDescription("description");
        informationDTO.setLink("link");
        informationDTO.setLinkTitle("linkTitle");
        informationDTO.setTermsAndConditions("termsAndConditions");
        informationDTO.setTitle("title");

        Map<String, BenefitInformationDTO> benefitInformationDTO = Maps.newHashMap();
        benefitInformationDTO.put("ES", informationDTO);

        dto.getBenefitInformation().putAll(benefitInformationDTO);
        Benefit benefit = this.instance.getPersistentObject(dto);

        this.assertBenefit(dto, benefit);
    }

    @Test(expected = ServiceException.class)
    public void getPersistentObject_unknownLanguage_throwServiceException() {
        BenefitDTO dto = this.createBenefitDTO();
        BenefitInformationDTO informationDTO = new BenefitInformationDTO();

        when(this.languageDAOMock.findByIsoCode("UNK")).thenReturn(null);

        Map<String, BenefitInformationDTO> benefitInformationDTO = Maps.newHashMap();
        benefitInformationDTO.put("UNK", informationDTO);

        dto.getBenefitInformation().putAll(benefitInformationDTO);
        this.instance.getPersistentObject(dto);

    }

    @Test(expected = ServiceException.class)
    public void getPersistentObject_unknownBenefitStatusCode_throwServiceException() {
        BenefitDTO dto = this.createBenefitDTO();
        dto.setBenefitStatusCode("UNK");
        when(this.benefitStatusDAOMock.findByCode("UNK")).thenReturn(null);

        this.instance.getPersistentObject(dto);

    }

    @Test(expected = ServiceException.class)
    public void getPersistentObject_unknownBenefitCategoryCode_throwServiceException() {
        BenefitDTO dto = this.createBenefitDTO();
        dto.setCategoryCode("UNK");

        when(this.benefitCategoryDAOMock.findByCode("UNK")).thenReturn((null));

        this.instance.getPersistentObject(dto);
    }

    @Test(expected = ServiceException.class)
    public void getPersistentObject_unknownProductCode_throwServiceException() {
        BenefitDTO dto = this.createBenefitDTO();

        BenefitApplianceDTO applianceDTO = new BenefitApplianceDTO();
        applianceDTO.setBrandCode("DESP");
        applianceDTO.setProductCode("UNK");
        applianceDTO.setOriginOID(234L);
        applianceDTO.setOriginType("T");
        applianceDTO.setDestinationOID(123L);
        applianceDTO.setDestinationType("T");

        dto.getAppliances().add(applianceDTO);

        when(this.productDAOMock.findByCode("UNK")).thenReturn((null));

        this.instance.getPersistentObject(dto);
    }

    @Test(expected = ServiceException.class)
    public void getPersistentObject_unknownGeoAreaOID_throwServiceException() {
        BenefitDTO dto = this.createBenefitDTO();

        BenefitApplianceDTO applianceDTO = new BenefitApplianceDTO();
        applianceDTO.setBrandCode("DESP");
        applianceDTO.setProductCode("PROD");
        applianceDTO.setOriginOID(0L);
        applianceDTO.setOriginType("T");
        applianceDTO.setDestinationOID(123L);
        applianceDTO.setDestinationType("T");

        dto.getAppliances().add(applianceDTO);

        when(this.productDAOMock.findByCode("UNK")).thenReturn((null));

        this.instance.getPersistentObject(dto);
    }

    @Test(expected = ServiceException.class)
    public void getPersistentObject_unknownBrandCode_throwServiceException() {
        BenefitDTO dto = this.createBenefitDTO();

        BenefitApplianceDTO applianceDTO = new BenefitApplianceDTO();
        applianceDTO.setBrandCode("UNK");
        applianceDTO.setProductCode("PROD");
        applianceDTO.setOriginOID(234L);
        applianceDTO.setOriginType("T");
        applianceDTO.setDestinationOID(123L);
        applianceDTO.setDestinationType("T");

        dto.getAppliances().add(applianceDTO);

        when(this.productDAOMock.findByCode("UNK")).thenReturn((null));

        this.instance.getPersistentObject(dto);
    }

    @Test
    public void getPersistentObject_nullBrandCode_returnBenefit() {
        BenefitDTO dto = this.createBenefitDTO();

        BenefitApplianceDTO applianceDTO = new BenefitApplianceDTO();
        applianceDTO.setBrandCode(null);
        applianceDTO.setProductCode("PROD");
        applianceDTO.setOriginOID(234L);
        applianceDTO.setOriginType("T");
        applianceDTO.setDestinationOID(123L);
        applianceDTO.setDestinationType("T");

        dto.getAppliances().add(applianceDTO);

        when(this.brandDAOMock.findByCode(null)).thenReturn((null));

        Benefit benefit = this.instance.getPersistentObject(dto);

        this.assertBenefit(dto, benefit);
    }

    @Test
    public void getPersistentObject_nullProductCode_returnBenefit() {
        BenefitDTO dto = this.createBenefitDTO();

        BenefitApplianceDTO applianceDTO = new BenefitApplianceDTO();
        applianceDTO.setBrandCode("DESP");
        applianceDTO.setProductCode(null);
        applianceDTO.setOriginOID(234L);
        applianceDTO.setOriginType("T");
        applianceDTO.setDestinationOID(123L);
        applianceDTO.setDestinationType("T");

        dto.getAppliances().add(applianceDTO);

        when(this.productDAOMock.findByCode(null)).thenReturn((null));

        Benefit benefit = this.instance.getPersistentObject(dto);

        this.assertBenefit(dto, benefit);
    }

    @Test
    public void getPersistentObject_nullGeoArea_returnBenefit() {
        BenefitDTO dto = this.createBenefitDTO();

        BenefitApplianceDTO applianceDTO = new BenefitApplianceDTO();
        applianceDTO.setBrandCode("DESP");
        applianceDTO.setProductCode("PROD");
        applianceDTO.setOriginOID(null);
        applianceDTO.setOriginType(null);
        applianceDTO.setDestinationOID(123L);
        applianceDTO.setDestinationType("T");

        dto.getAppliances().add(applianceDTO);

        when(this.geoAreaMock.getGeoAreaByItemOID(null)).thenReturn((null));

        Benefit benefit = this.instance.getPersistentObject(dto);

        this.assertBenefit(dto, benefit);
    }

    @Test
    public void fillDTO_correct_returnsDTO() {

        Benefit benefit = ModelContentCreator.createBenefit();
        ListMultimap<Long, GeoAreaDescriptionDTO> geoAreaDescriptions = ArrayListMultimap.create();

        BenefitDTO dto = this.instance.getDTO(benefit, geoAreaDescriptions);

        this.assertBenefit(dto, benefit);
    }

    @Test
    public void fillDTO_geoAreaDescriptions_returnsDTO() {

        Benefit benefit = ModelContentCreator.createBenefit();
        ListMultimap<Long, GeoAreaDescriptionDTO> geoAreaDescriptions = ArrayListMultimap.create();

        GeoAreaDescriptionDTO descDTO = new GeoAreaDescriptionDTO();
        descDTO.setLanguageCode("ES");
        descDTO.setName("Buenos Aires");
        for (Appliance appliance : benefit.getAppliance()) {
            if (appliance.getDestinationGeoArea() != null) {
                geoAreaDescriptions.put(appliance.getDestinationGeoArea().getDespegarItemOID(), descDTO);
            }
        }

        BenefitDTO dto = this.instance.getDTO(benefit, geoAreaDescriptions);

        this.assertBenefit(dto, benefit);
    }

    private void assertBenefit(BenefitDTO dto, Benefit benefit) {
        assertEquals(dto.getBenefitStatusCode(), benefit.getBenefitStatus().getCode());
        assertEquals(dto.getCategoryCode(), benefit.getBenefitCategory().getCode());
        assertEquals(dto.getCompanyOID(), benefit.getCompany().getOID());
        assertEquals(dto.getDateFrom(), Long.valueOf(benefit.getDateFrom().getTime()));
        assertEquals(dto.getDateTo(), Long.valueOf(benefit.getDateTo().getTime()));
        assertEquals(dto.getIsFree(), benefit.getIsFree());
        assertEquals(dto.getIsOutstanding(), benefit.getIsOutstanding());
        assertEquals(dto.getBenefitStatusCode(), benefit.getBenefitStatus().getCode());
        assertEquals(dto.getOID(), benefit.getOID());
        assertEquals(dto.getExternalResources().size(), benefit.getExternalResource().size());

        assertEquals(dto.getAppliances().size(), benefit.getAppliance().size());
        Appliance appliance = null;
        GeoArea destinationGeoArea = null;
        GeoArea originGeoArea = null;
        Brand brand = null;
        Product product = null;

        for (final BenefitApplianceDTO benefitApplianceDTO : dto.getAppliances()) {
            appliance = Iterables.find(benefit.getAppliance(), new Predicate<Appliance>() {

                @Override
                public boolean apply(Appliance input) {

                    boolean sameBrand = (input.getBrand() != null)
                        && benefitApplianceDTO.getBrandCode() == input.getBrand().getCode();
                    boolean sameDestination = (input.getDestinationGeoArea() != null)
                        && benefitApplianceDTO.getDestinationOID() == input.getDestinationGeoArea().getDespegarItemOID();
                    boolean sameOrigin = (input.getOriginGeoArea() != null)
                        && benefitApplianceDTO.getOriginOID() == input.getOriginGeoArea().getDespegarItemOID();
                    boolean sameProduct = (input.getProduct() != null)
                        && benefitApplianceDTO.getProductCode() == input.getProduct().getCode();
                    return sameBrand || sameDestination || sameOrigin || sameProduct;
                }
            });

            destinationGeoArea = appliance.getDestinationGeoArea();
            originGeoArea = appliance.getOriginGeoArea();
            brand = appliance.getBrand();
            product = appliance.getProduct();

            assertEquals(benefitApplianceDTO.getBrandCode(), (brand == null) ? null : brand.getCode());

            assertEquals(benefitApplianceDTO.getDestinationOID(),
                (destinationGeoArea == null) ? null : destinationGeoArea.getDespegarItemOID());
            assertEquals(benefitApplianceDTO.getDestinationType(),
                (destinationGeoArea == null) ? null : destinationGeoArea.getType());
            assertEquals(benefitApplianceDTO.getOriginOID(),
                (originGeoArea == null) ? null : originGeoArea.getDespegarItemOID());
            assertEquals(benefitApplianceDTO.getOriginType(), (originGeoArea == null) ? null : originGeoArea.getType());
            assertEquals(benefitApplianceDTO.getProductCode(), (product == null) ? null : product.getCode());
        }

        assertEquals(dto.getExternalResources().size(), benefit.getExternalResource().size());
        VoucherI18N voucherI18N = null;
        String dtoVoucherName = null;
        for (int i = 0; i < benefit.getExternalResource().size(); i++) {
            voucherI18N = Iterables.get(benefit.getExternalResource(), i);
            dtoVoucherName = dto.getExternalResources().get(voucherI18N.getLanguage().getIsoCode());
            assertNotNull(dtoVoucherName);
            assertEquals(dtoVoucherName, voucherI18N.getResourceName());
        }

        BenefitInformationDTO benefitDTO = null;
        BenefitDescriptionI18N benefitDescriptionI18N = null;
        for (int i = 0; i < benefit.getBenefitDescriptionI18N().size(); i++) {
            benefitDescriptionI18N = Iterables.get(benefit.getBenefitDescriptionI18N(), i);
            benefitDTO = dto.getBenefitInformation().get(benefitDescriptionI18N.getLanguage().getIsoCode());
            assertNotNull(benefitDTO);
            assertEquals(benefitDTO.getBranches(), benefitDescriptionI18N.getBranches());
            assertEquals(benefitDTO.getDescription(), benefitDescriptionI18N.getDescription());
            assertEquals(benefitDTO.getLink(), benefitDescriptionI18N.getLink());
            assertEquals(benefitDTO.getLinkTitle(), benefitDescriptionI18N.getLinkTitle());
            assertEquals(benefitDTO.getTermsAndConditions(), benefitDescriptionI18N.getTermsAndConditions());
            assertEquals(benefitDTO.getTitle(), benefitDescriptionI18N.getTitle());
        }

    }

    public BenefitDTO createBenefitDTO() {
        BenefitDTO dto = new BenefitDTO();
        dto.setAppliances(new HashSet<BenefitApplianceDTO>());
        dto.setBenefitInformation(new HashMap<String, BenefitInformationDTO>());
        dto.setBenefitStatusCode(BenefitStatusCode.PUBLISHED.getCode());
        dto.setCategoryCode("CAT");
        dto.setCompanyOID(123L);
        dto.setDateFrom(System.currentTimeMillis());
        dto.setDateTo(System.currentTimeMillis());
        dto.setIsFree(true);
        dto.setIsOutstanding(true);
        dto.setExternalResources(new HashMap<String, String>());
        return dto;
    }
}
