package com.despegar.sobek.translator;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import com.despegar.sobek.exception.SobekServiceException;
import com.despegar.sobek.model.Appliance;
import com.despegar.sobek.model.Benefit;
import com.despegar.sobek.model.BenefitDescriptionI18N;
import com.despegar.sobek.model.Company;
import com.despegar.sobek.model.GeoArea;
import com.despegar.sobek.model.VoucherI18N;
import com.despegar.sobek.solr.index.data.GeoAreaMemorySnapshot;
import com.despegar.sobek.solr.model.BenefitIndex;
import com.google.common.collect.Lists;

public class BenefitIndexSolrTranslator {

    private static Logger logger = Logger.getLogger(BenefitIndexSolrTranslator.class);

    private static final String ALL = "ALL";
    private static final String ES = "ES";
    private static final String PT = "PT";

    private GeoAreaMemorySnapshot geoAreaMemorySnapshot;

    public List<BenefitIndex> translatorList(List<Benefit> benefits) {

        List<BenefitIndex> benefitIndexs = new LinkedList<BenefitIndex>();
        for (Benefit benefit : benefits) {
            benefitIndexs.addAll(this.translateBenefit(benefit));
        }
        return benefitIndexs;
    }

    private List<BenefitIndex> translateBenefit(Benefit benefit) {

        List<BenefitIndex> benefitIndexs = Lists.newArrayList();
        String brandCode = null;
        String productCode = null;
        BenefitIndex benefitIndex = null;
        BenefitIndex baseBenefitIndex = this.translateBaseBenefit(benefit);

        for (Appliance appliance : benefit.getAppliance()) {
            try {
                benefitIndex = (BenefitIndex) BeanUtils.cloneBean(baseBenefitIndex);
            } catch (Exception e) {
                String message = "Could not translate Benefit into BenefitIndex";
                logger.error(message, e);
                throw new SobekServiceException(message, e);
            }

            benefitIndex.setOidAppliance(String.valueOf(appliance.getOID()));

            brandCode = (appliance.getBrand() == null) ? ALL : appliance.getBrand().getCode();
            productCode = (appliance.getProduct() == null) ? ALL : appliance.getProduct().getCode();

            benefitIndex.setBrand(brandCode);
            benefitIndex.setProduct(productCode);

            GeoAreaIndex geoAreaIndex = this.translateGeoArea(appliance.getOriginGeoArea());
            benefitIndex.setCitiesFrom(geoAreaIndex.getCities());
            benefitIndex.setCountryFrom(geoAreaIndex.getCountryOID());

            geoAreaIndex = this.translateGeoArea(appliance.getDestinationGeoArea());
            benefitIndex.setCitiesTo(geoAreaIndex.getCities());
            benefitIndex.setCountryTo(geoAreaIndex.getCountryOID());

            benefitIndexs.add(benefitIndex);
        }

        return benefitIndexs;
    }

    private BenefitIndex translateBaseBenefit(Benefit benefit) {
        BenefitIndex benefitIndex = new BenefitIndex();
        String languageIsoCode;
        if (benefit.getBenefitCategory() != null) {
            benefitIndex.setCategory(benefit.getBenefitCategory().getCode());
        }
        if (benefit.getLinkTemplateType() != null) {
            benefitIndex.setLinkTemplateType(benefit.getLinkTemplateType().toString());
        }

        for (VoucherI18N voucherDescription : benefit.getExternalResource()) {
            languageIsoCode = voucherDescription.getLanguage().getIsoCode();
            if (ES.equals(languageIsoCode)) {
                benefitIndex.setLinkVoucherES(voucherDescription.getResourceName());
            }
            if (PT.equals(languageIsoCode)) {
                benefitIndex.setLinkVoucherPT(voucherDescription.getResourceName());
            }
        }

        for (BenefitDescriptionI18N benefitDescription : benefit.getBenefitDescriptionI18N()) {
            languageIsoCode = benefitDescription.getLanguage().getIsoCode();
            if (ES.equals(languageIsoCode)) {
                benefitIndex.setBranchesES(benefitDescription.getBranches());
                benefitIndex.setLinkTitleES(benefitDescription.getLinkTitle());
                benefitIndex.setTermsAndConditionsES(benefitDescription.getTermsAndConditions());
                benefitIndex.setTitleES(benefitDescription.getTitle());
                benefitIndex.setLinkES(benefitDescription.getLink());
                benefitIndex.setDescriptionES(benefitDescription.getDescription());
            }
            if (PT.equals(languageIsoCode)) {
                benefitIndex.setBranchesPT(benefitDescription.getBranches());
                benefitIndex.setLinkTitlePT(benefitDescription.getLinkTitle());
                benefitIndex.setTermsAndConditionsPT(benefitDescription.getTermsAndConditions());
                benefitIndex.setTitlePT(benefitDescription.getTitle());
                benefitIndex.setLinkPT(benefitDescription.getLink());
                benefitIndex.setDescriptionPT(benefitDescription.getDescription());
            }
        }
        Company company = benefit.getCompany();
        if (company != null) {
            benefitIndex.setCompany(company.getOID());
            benefitIndex.setCompanyName(company.getName());
            if (company.getPicture() != null) {
                benefitIndex.setCompanyPicture(company.getPicture().getResourceName());
            }
        }

        benefitIndex.setDateFrom(benefit.getDateFrom());
        benefitIndex.setDateTo(benefit.getDateTo());
        benefitIndex.setIsFree(benefit.getIsFree());
        benefitIndex.setIsOutstanding(benefit.getIsOutstanding());
        benefitIndex.setOidBenefit(benefit.getOID().toString());
        benefitIndex.setRelevance(benefit.getRelevance().intValue());
        benefitIndex.setState(benefit.getBenefitStatus().getCode());
        benefitIndex.setPublicationDate(benefit.getPublicationDate());

        return benefitIndex;
    }

    private GeoAreaIndex translateGeoArea(GeoArea geoArea) {
        GeoAreaIndex geoAreaIndex = new GeoAreaIndex();
        Long countryOID = 0L;
        List<Long> cities = new LinkedList<Long>();

        if (geoArea == null) {
            cities.add(0L);
        } else {
            if (geoArea.getType().equalsIgnoreCase("C")) {
                // Voy a buscar el pais para el que aplica
                cities.add(geoArea.getDespegarItemOID());
                countryOID = this.getCountry(geoArea.getDespegarItemOID());
            } else if (geoArea.getType().equalsIgnoreCase("P")) {
                // Voy a buscar las ciudades para el que aplica
                countryOID = geoArea.getDespegarItemOID();
                cities.addAll(this.getCities(countryOID));
            }
        }

        geoAreaIndex.setCities(cities);
        geoAreaIndex.setCountryOID(countryOID);
        return geoAreaIndex;
    }

    private Long getCountry(Long oid) {
        return this.geoAreaMemorySnapshot.getCountry(oid);
    }

    private List<Long> getCities(Long oid) {
        return this.geoAreaMemorySnapshot.getCities(oid);
    }

    public void setGeoAreaMemorySnapshot(GeoAreaMemorySnapshot geoAreaMemorySnapshot) {
        this.geoAreaMemorySnapshot = geoAreaMemorySnapshot;
    }

    protected static class GeoAreaIndex {

        private List<Long> cities;
        private Long countryOID;

        public List<Long> getCities() {
            return this.cities;
        }

        public Long getCountryOID() {
            return this.countryOID;
        }

        public void setCities(List<Long> cities) {
            this.cities = cities;
        }

        public void setCountryOID(Long countryOID) {
            this.countryOID = countryOID;
        }
    }
}
