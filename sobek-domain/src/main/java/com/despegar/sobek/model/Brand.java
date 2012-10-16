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
@Table(name = "BRAND")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, include = "all", region = "com.despegar.sobek.model.Brand")
public class Brand
    extends AbstractCatalogue<BrandDescriptionI18N> {

    private static final long serialVersionUID = 1L;
    private Set<BrandDescriptionI18N> descriptions;

    @OneToMany(fetch = FetchType.LAZY)
    @Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "BRAND_OID", nullable = false)
    @ForeignKey(name = "FK_BRAND_DESCRIPTION_I18N__BRAND")
    @Index(name = "IDX_BRAND_DESCRIPTION_I18N__BRAND_OID")
    public Set<BrandDescriptionI18N> getDescriptions() {
        if (this.descriptions == null) {
            this.descriptions = new HashSet<BrandDescriptionI18N>();
        }
        return this.descriptions;
    }

    public void setDescriptions(Set<BrandDescriptionI18N> descriptions) {
        this.descriptions = descriptions;
    }

}
