package com.despegar.sobek.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

@Entity
@Table(name = "BENEFIT_STATUS")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, include = "all", region = "com.despegar.sobek.model.BenefitStatus")
public class BenefitStatus
    extends AbstractCatalogue<BenefitStatusDescriptionI18N> {

    private static final long serialVersionUID = 1L;
    private Set<BenefitStatusDescriptionI18N> descriptions;

    @OneToMany(fetch = FetchType.LAZY)
    @Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "BENEFIT_STATUS_OID", nullable = false)
    @ForeignKey(name = "FK_BENEFIT_STATUS_DESCRIPTION_I18N__BENEFIT_STATUS")
    @Index(name = "IDX_BENEFIT_STATUS_DESCRIPTION_I18N__BENEFIT_STATUS_OID")
    public Set<BenefitStatusDescriptionI18N> getDescriptions() {
        if (this.descriptions == null) {
            this.descriptions = new HashSet<BenefitStatusDescriptionI18N>();
        }
        return this.descriptions;
    }

    public void setDescriptions(Set<BenefitStatusDescriptionI18N> descriptions) {
        this.descriptions = descriptions;
    }

}
