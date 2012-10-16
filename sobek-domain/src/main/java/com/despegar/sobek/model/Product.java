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
@Table(name = "PRODUCT")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, include = "all", region = "com.despegar.sobek.model.Product")
public class Product
    extends AbstractCatalogue<ProductDescriptionI18N> {

    private static final long serialVersionUID = 1L;
    private Set<ProductDescriptionI18N> descriptions;

    @OneToMany(fetch = FetchType.LAZY)
    @Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "PRODUCT_OID", nullable = false)
    @ForeignKey(name = "FK_PRODUCT_DESCRIPTION_I18N__PRODUCT")
    @Index(name = "IDX_PRODUCT_DESCRIPTION_I18N__PRODUCT_OID")
    public Set<ProductDescriptionI18N> getDescriptions() {
        if (this.descriptions == null) {
            this.descriptions = new HashSet<ProductDescriptionI18N>();
        }
        return this.descriptions;
    }

    public void setDescriptions(Set<ProductDescriptionI18N> descriptions) {
        this.descriptions = descriptions;
    }
}
