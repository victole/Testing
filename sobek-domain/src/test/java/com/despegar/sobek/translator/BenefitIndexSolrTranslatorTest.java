package com.despegar.sobek.translator;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import com.despegar.framework.tests.spring.AbstractTransactionalSpringTest;
import com.despegar.sobek.model.Appliance;
import com.despegar.sobek.model.Benefit;
import com.despegar.sobek.model.BenefitDescriptionI18N;
import com.despegar.sobek.model.Company;
import com.despegar.sobek.model.GeoArea;
import com.despegar.sobek.model.Language;
import com.despegar.sobek.model.Picture;
import com.despegar.sobek.solr.model.BenefitIndex;
import com.despegar.sobek.utility.ModelContentCreator;

@ContextConfiguration(locations = {"classpath:/com/despegar/test/test-reference-data-context.xml"})
public class BenefitIndexSolrTranslatorTest
    extends AbstractTransactionalSpringTest {

    @Resource
    BenefitIndexSolrTranslator benefitIndexSolrTranslator;

    Benefit benefit1;

    @Before
    public void setUp() {
        this.benefit1 = ModelContentCreator.createBenefit();
        this.benefit1.setOID(1L);
    }

    @Test
    public void translate_CompanyPicture_returnBenefitList() {
        this.setApplianceOID();
        List<Benefit> benefits = new LinkedList<Benefit>();
        Company company = new Company();
        Picture picture = new Picture();
        picture.setResourceName("FOTO");
        company.setPicture(picture);
        this.benefit1.setCompany(company);
        benefits.add(this.benefit1);
        List<BenefitIndex> translatorList = this.benefitIndexSolrTranslator.translatorList(benefits);
        for (BenefitIndex benefitIndex : translatorList) {
            Assert.assertEquals(benefitIndex.getCompanyPicture(), "FOTO");
        }
    }

    @Test
    public void translate_brandNull_returnBenefitList() {
        this.setApplianceOID();
        Appliance appliance = this.benefit1.getAppliance().iterator().next();
        this.benefit1.getAppliance().clear();
        this.benefit1.getAppliance().add(appliance);
        this.benefit1.getAppliance().iterator().next().setBrand(null);

        List<Benefit> benefits = new LinkedList<Benefit>();
        benefits.add(this.benefit1);
        List<BenefitIndex> translatorList = this.benefitIndexSolrTranslator.translatorList(benefits);
        for (BenefitIndex benefitIndex : translatorList) {
            Assert.assertEquals(benefitIndex.getBrand(), "ALL");
        }
    }

    @Test
    public void translate_DestinationGeoAreaAndOriginGeoAreaToCity_returnBenefitList() {
        this.setApplianceOID();
        GeoArea destinationGeoArea = new GeoArea();
        destinationGeoArea.setDespegarItemOID(982L);
        destinationGeoArea.setType("C");
        Appliance appliance = this.benefit1.getAppliance().iterator().next();
        this.benefit1.getAppliance().clear();
        this.benefit1.getAppliance().add(appliance);
        this.benefit1.getAppliance().iterator().next().setDestinationGeoArea(destinationGeoArea);
        this.benefit1.getAppliance().iterator().next().setOriginGeoArea(destinationGeoArea);

        List<Benefit> benefits = new LinkedList<Benefit>();
        benefits.add(this.benefit1);
        List<BenefitIndex> translatorList = this.benefitIndexSolrTranslator.translatorList(benefits);
        for (BenefitIndex benefitIndex : translatorList) {
            Assert.assertEquals(benefitIndex.getCitiesTo().get(0).toString(), "982");
            Assert.assertEquals(benefitIndex.getCitiesFrom().get(0).toString(), "982");
        }
    }

    @Test
    public void translate_DestinationGeoAreaAndOriginGeoAreaToCountry_returnBenefitList() {
        this.setApplianceOID();
        GeoArea destinationGeoArea = new GeoArea();
        destinationGeoArea.setDespegarItemOID(982L);
        destinationGeoArea.setType("P");
        Appliance appliance = this.benefit1.getAppliance().iterator().next();
        this.benefit1.getAppliance().clear();
        this.benefit1.getAppliance().add(appliance);
        this.benefit1.getAppliance().iterator().next().setDestinationGeoArea(destinationGeoArea);
        this.benefit1.getAppliance().iterator().next().setOriginGeoArea(destinationGeoArea);

        List<Benefit> benefits = new LinkedList<Benefit>();
        benefits.add(this.benefit1);
        List<BenefitIndex> translatorList = this.benefitIndexSolrTranslator.translatorList(benefits);
        for (BenefitIndex benefitIndex : translatorList) {
            Assert.assertEquals(benefitIndex.getCountryTo().toString(), "982");
            Assert.assertEquals(benefitIndex.getCountryFrom().toString(), "982");
        }
    }

    @Test
    public void translate_DestinationGeoAreaAndOriginGeoAreaNULL_returnBenefitList() {
        this.setApplianceOID();
        List<Benefit> benefits = new LinkedList<Benefit>();
        benefits.add(this.benefit1);

        Appliance appliance = this.benefit1.getAppliance().iterator().next();
        this.benefit1.getAppliance().clear();
        this.benefit1.getAppliance().add(appliance);
        this.benefit1.getAppliance().iterator().next().setDestinationGeoArea(null);
        this.benefit1.getAppliance().iterator().next().setOriginGeoArea(null);
        benefits.add(this.benefit1);

        List<BenefitIndex> translatorList = this.benefitIndexSolrTranslator.translatorList(benefits);
        for (BenefitIndex benefitIndex : translatorList) {
            Assert.assertEquals(benefitIndex.getCitiesTo().get(0).toString(), "0");
            Assert.assertEquals(benefitIndex.getCountryFrom().toString(), "0");
        }
    }

    @Test
    public void translate_BenefitDescription_returnBenefitList() {
        this.setApplianceOID();
        BenefitDescriptionI18N e = new BenefitDescriptionI18N();
        e.setBranches("Branch PT");
        e.setDescription("descriptionPT");
        Language language = new Language();
        language.setIsoCode("PT");
        language.setName("Portuges");
        e.setLanguage(language);
        e.setLink("wwww");
        this.benefit1.getBenefitDescriptionI18N().add(e);
        List<Benefit> benefits = new LinkedList<Benefit>();
        benefits.add(this.benefit1);
        List<BenefitIndex> translatorList = this.benefitIndexSolrTranslator.translatorList(benefits);
        for (BenefitIndex benefitIndex : translatorList) {
            Assert.assertEquals(benefitIndex.getBranchesPT(), "Branch PT");
            Assert.assertEquals(benefitIndex.getDescriptionPT(), "descriptionPT");
            Assert.assertEquals(benefitIndex.getLinkPT(), "wwww");
        }
    }

    @Test
    public void translate_BenefitDescriptionNULL_returnBenefitList() {
        this.setApplianceOID();
        this.benefit1.getBenefitDescriptionI18N().clear();
        List<Benefit> benefits = new LinkedList<Benefit>();
        benefits.add(this.benefit1);
        List<BenefitIndex> translatorList = this.benefitIndexSolrTranslator.translatorList(benefits);
        for (BenefitIndex benefitIndex : translatorList) {
            Assert.assertEquals(benefitIndex.getBranchesES(), null);
            Assert.assertEquals(benefitIndex.getDescriptionES(), null);
            Assert.assertEquals(benefitIndex.getLinkES(), null);
            Assert.assertEquals(benefitIndex.getBranchesPT(), null);
            Assert.assertEquals(benefitIndex.getDescriptionPT(), null);
            Assert.assertEquals(benefitIndex.getLinkPT(), null);
        }
    }

    @Test
    public void translate_appliance_returnBenefitList() {
        this.setApplianceOID();
        List<Benefit> benefits = new LinkedList<Benefit>();
        benefits.add(this.benefit1);
        List<BenefitIndex> translatorList = this.benefitIndexSolrTranslator.translatorList(benefits);
        for (BenefitIndex benefitIndex : translatorList) {
            Assert.assertEquals(benefitIndex.getBranchesES(), "Branch N1");
            Assert.assertEquals(benefitIndex.getCategory(), "GST");
            Assert.assertEquals(benefitIndex.getDescriptionES(), "Description");
            Assert.assertEquals(benefitIndex.getIsFree(), true);
            Assert.assertEquals(benefitIndex.getIsOutstanding(), true);
            Assert.assertEquals(benefitIndex.getLinkES(), "www.link.com");
            Assert.assertEquals(benefitIndex.getLinkVoucherES(), "mypdf.pdf");
            Assert.assertEquals(benefitIndex.getLinkVoucherPT(), null);
            Assert.assertEquals(benefitIndex.getRelevance().toString(), BigDecimal.TEN.toString());
            Assert.assertEquals(benefitIndex.getTermsAndConditionsES(), "terms & conditions");
            Assert.assertEquals(benefitIndex.getTermsAndConditionsPT(), null);
            Assert.assertEquals(benefitIndex.getState(), "PUB");
            Assert.assertEquals(benefitIndex.getTitleES(), "title");
            Assert.assertEquals(benefitIndex.getTitlePT(), null);
        }
    }


    private void setApplianceOID() {
        if (this.benefit1.getAppliance() != null) {
            List<Appliance> appliances = new LinkedList<Appliance>();
            Long oid = 1L;
            for (Appliance appliance : this.benefit1.getAppliance()) {
                appliance.setOID(oid);
                appliances.add(appliance);
                oid++;
            }
            this.benefit1.getAppliance().clear();
            this.benefit1.getAppliance().addAll(appliances);
        }
    }
}
