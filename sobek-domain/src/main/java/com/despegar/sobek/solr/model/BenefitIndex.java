package com.despegar.sobek.solr.model;

import java.util.Date;
import java.util.List;

import org.apache.solr.client.solrj.beans.Field;

public class BenefitIndex {

    @Field
    private String oidAppliance;

    @Field
    private String oidBenefit;

    @Field
    private String linkES;

    @Field
    private String linkPT;

    @Field
    private String linkTitleES;

    @Field
    private String linkTitlePT;

    @Field
    private String branchesES;

    @Field
    private String branchesPT;

    @Field
    private String termsAndConditionsES;

    @Field
    private String termsAndConditionsPT;

    @Field
    private String titleES;

    @Field
    private String titlePT;

    @Field
    private Long company;

    @Field
    private String companyName;

    @Field
    private String companyPicture;

    @Field
    private String category;

    @Field
    private Boolean isFree;

    @Field
    private Boolean isOutstanding;

    @Field
    private String state;

    @Field
    private Date dateFrom;

    @Field
    private Date dateTo;

    @Field
    private Integer relevance;

    @Field
    private Date publicationDate;

    @Field
    private String brand;

    @Field
    private String product;

    @Field
    private List<Long> citiesFrom;

    @Field
    private Long countryFrom;

    @Field
    private List<Long> citiesTo;

    @Field
    private Long countryTo;

    @Field
    private String linkTemplateType;

    @Field
    private String linkVoucherES;

    @Field
    private String linkVoucherPT;

    @Field
    private String descriptionES;

    @Field
    private String descriptionPT;

    public String getCompanyPicture() {
        return this.companyPicture;
    }

    public void setCompanyPicture(String companyPicture) {
        this.companyPicture = companyPicture;
    }

    public String getCompanyName() {
        return this.companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getDescriptionES() {
        return this.descriptionES;
    }

    public void setDescriptionES(String descriptionES) {
        this.descriptionES = descriptionES;
    }

    public String getDescriptionPT() {
        return this.descriptionPT;
    }

    public void setDescriptionPT(String descriptionPT) {
        this.descriptionPT = descriptionPT;
    }

    public String getLinkVoucherES() {
        return this.linkVoucherES;
    }

    public void setLinkVoucherES(String linkVoucherES) {
        this.linkVoucherES = linkVoucherES;
    }

    public String getLinkVoucherPT() {
        return this.linkVoucherPT;
    }

    public void setLinkVoucherPT(String linkVoucherPT) {
        this.linkVoucherPT = linkVoucherPT;
    }

    public String getLinkTemplateType() {
        return this.linkTemplateType;
    }

    public void setLinkTemplateType(String linkTemplateType) {
        this.linkTemplateType = linkTemplateType;
    }

    public String getOidAppliance() {
        return this.oidAppliance;
    }

    public void setOidAppliance(String oidAppliance) {
        this.oidAppliance = oidAppliance;
    }

    public String getOidBenefit() {
        return this.oidBenefit;
    }

    public void setOidBenefit(String oidBenefit) {
        this.oidBenefit = oidBenefit;
    }


    public String getLinkES() {
        return this.linkES;
    }

    public void setLinkES(String linkES) {
        this.linkES = linkES;
    }

    public String getLinkPT() {
        return this.linkPT;
    }

    public void setLinkPT(String linkPT) {
        this.linkPT = linkPT;
    }

    public Long getCompany() {
        return this.company;
    }

    public void setCompany(Long company) {
        this.company = company;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Boolean getIsFree() {
        return this.isFree;
    }

    public void setIsFree(Boolean isFree) {
        this.isFree = isFree;
    }

    public Boolean getIsOutstanding() {
        return this.isOutstanding;
    }

    public void setIsOutstanding(Boolean isOutstanding) {
        this.isOutstanding = isOutstanding;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getDateFrom() {
        return this.dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        return this.dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public Integer getRelevance() {
        return this.relevance;
    }

    public void setRelevance(Integer relevance) {
        this.relevance = relevance;
    }

    public Date getPublicationDate() {
        return this.publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getBrand() {
        return this.brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getProduct() {
        return this.product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public List<Long> getCitiesFrom() {
        return this.citiesFrom;
    }

    public void setCitiesFrom(List<Long> citiesFrom) {
        this.citiesFrom = citiesFrom;
    }

    public List<Long> getCitiesTo() {
        return this.citiesTo;
    }

    public void setCitiesTo(List<Long> citiesTo) {
        this.citiesTo = citiesTo;
    }

    public Long getCountryFrom() {
        return this.countryFrom;
    }

    public void setCountryFrom(Long countryFrom) {
        this.countryFrom = countryFrom;
    }

    public Long getCountryTo() {
        return this.countryTo;
    }

    public void setCountryTo(Long countryTo) {
        this.countryTo = countryTo;
    }

    public String getLinkTitleES() {
        return this.linkTitleES;
    }

    public void setLinkTitleES(String linkTitleES) {
        this.linkTitleES = linkTitleES;
    }

    public String getLinkTitlePT() {
        return this.linkTitlePT;
    }

    public void setLinkTitlePT(String linkTitlePT) {
        this.linkTitlePT = linkTitlePT;
    }

    public String getBranchesES() {
        return this.branchesES;
    }

    public void setBranchesES(String branchesES) {
        this.branchesES = branchesES;
    }

    public String getBranchesPT() {
        return this.branchesPT;
    }

    public void setBranchesPT(String branchesPT) {
        this.branchesPT = branchesPT;
    }

    public String getTermsAndConditionsES() {
        return this.termsAndConditionsES;
    }

    public void setTermsAndConditionsES(String termsAndConditionsES) {
        this.termsAndConditionsES = termsAndConditionsES;
    }

    public String getTermsAndConditionsPT() {
        return this.termsAndConditionsPT;
    }

    public void setTermsAndConditionsPT(String termsAndConditionsPT) {
        this.termsAndConditionsPT = termsAndConditionsPT;
    }

    public String getTitleES() {
        return this.titleES;
    }

    public void setTitleES(String titleES) {
        this.titleES = titleES;
    }

    public String getTitlePT() {
        return this.titlePT;
    }

    public void setTitlePT(String titlePT) {
        this.titlePT = titlePT;
    }


}
