package com.despegar.sobek.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.despegar.framework.persistence.AbstractIdentifiablePersistentObjectWithLogicalDeletion;

@Entity
@Table(name = "BENEFIT")
public class Benefit
    extends AbstractIdentifiablePersistentObjectWithLogicalDeletion {

    private static final long serialVersionUID = 1L;
    private Date dateFrom;
    private Date dateTo;
    private Boolean isFree;
    private Boolean isOutstanding;
    private BigDecimal relevance;
    private BenefitStatus benefitStatus;
    private Set<BenefitDescriptionI18N> benefitDescriptionI18N;
    private Set<VoucherI18N> externalResource;
    private Set<Appliance> appliance;
    private Company company;
    private BenefitCategory benefitCategory;
    private Date publicationDate;
    private BenefitLinkRenderType linkTemplateType;

    @Column(name = "DATE_FROM")
    public Date getDateFrom() {
        return this.dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    @Column(name = "DATE_TO")
    public Date getDateTo() {
        return this.dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    @Column(name = "IS_FREE")
    public Boolean getIsFree() {
        return this.isFree;
    }

    public void setIsFree(Boolean isFree) {
        this.isFree = isFree;
    }

    @Column(name = "IS_OUTSTANDING")
    public Boolean getIsOutstanding() {
        return this.isOutstanding;
    }

    public void setIsOutstanding(Boolean isOutstanding) {
        this.isOutstanding = isOutstanding;
    }

    @Column(name = "RELEVANCE", columnDefinition = "numeric(12, 2)")
    public BigDecimal getRelevance() {
        return this.relevance;
    }

    public void setRelevance(BigDecimal relevance) {
        this.relevance = relevance;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BENEFIT_STATUS_OID")
    @ForeignKey(name = "FK_BENEFIT__BENEFIT_STATUS")
    @Index(name = "IDX_BENEFIT__BENEFIT_STATUS_OID")
    public BenefitStatus getBenefitStatus() {
        return this.benefitStatus;
    }

    public void setBenefitStatus(BenefitStatus benefitStatus) {
        this.benefitStatus = benefitStatus;
    }

    @OneToMany(fetch = FetchType.LAZY)
    @Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "BENEFIT_OID", nullable = false)
    @ForeignKey(name = "FK_BENEFIT_DESCRIPTION_I18N__BENEFIT")
    @Index(name = "IDX_BENEFIT_DESCRIPTION_I18N__BENEFIT_OID")
    public Set<BenefitDescriptionI18N> getBenefitDescriptionI18N() {
        if (this.benefitDescriptionI18N == null) {
            this.benefitDescriptionI18N = new HashSet<BenefitDescriptionI18N>();
        }
        return this.benefitDescriptionI18N;
    }

    public void setBenefitDescriptionI18N(Set<BenefitDescriptionI18N> benefitDescriptionI18N) {
        this.benefitDescriptionI18N = benefitDescriptionI18N;
    }

    public void addBenefitDescriptionI18N(BenefitDescriptionI18N benefitDescriptionI18N) {
        this.getBenefitDescriptionI18N().add(benefitDescriptionI18N);
    }

    @OneToMany(fetch = FetchType.LAZY)
    @Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "BENEFIT_OID", nullable = false)
    @ForeignKey(name = "FK_VOUCHER_I18N__BENEFIT")
    @Index(name = "IDX_VOUCHER_I18N__BENEFIT_OID")
    public Set<VoucherI18N> getExternalResource() {
        if (this.externalResource == null) {
            this.externalResource = new HashSet<VoucherI18N>();
        }
        return this.externalResource;
    }

    public void setExternalResource(Set<VoucherI18N> voucherI18N) {
        this.externalResource = voucherI18N;
    }

    public void addExternalResource(VoucherI18N voucherI18N) {
        this.getExternalResource().add(voucherI18N);
    }

    @OneToMany(fetch = FetchType.LAZY)
    @Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "BENEFIT_OID", nullable = false)
    @ForeignKey(name = "FK_APPLIANCE__BENEFIT")
    @Index(name = "IDX_APPLIANCE__BENEFIT_OID")
    public Set<Appliance> getAppliance() {
        if (this.appliance == null) {
            this.appliance = new HashSet<Appliance>();
        }
        return this.appliance;
    }

    public void setAppliance(Set<Appliance> appliance) {
        this.appliance = appliance;
    }

    public void addAppliance(Appliance appliance) {
        this.getAppliance().add(appliance);
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_OID")
    @ForeignKey(name = "FK_BENEFIT__COMPANY")
    @Index(name = "IDX_BENEFIT__COMPANY_OID")
    public Company getCompany() {
        return this.company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BENEFIT_CATEGORY_OID")
    @ForeignKey(name = "FK_BENEFIT__BENEFIT_CATEGORY")
    @Index(name = "IDX_BENEFIT__BENEFIT_CATEGORY_OID")
    public BenefitCategory getBenefitCategory() {
        return this.benefitCategory;
    }

    public void setBenefitCategory(BenefitCategory BenefitCategory) {
        this.benefitCategory = BenefitCategory;
    }

    @Column(name = "PUBLICATION_DATE")
    public Date getPublicationDate() {
        return this.publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "LINK_TEMPLATE_TYPE", nullable = false)
    public BenefitLinkRenderType getLinkTemplateType() {
        return this.linkTemplateType;
    }

    public void setLinkTemplateType(BenefitLinkRenderType linkTemplateType) {
        this.linkTemplateType = linkTemplateType;
    }
}
