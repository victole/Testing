package com.despegar.sobek.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "BENEFIT_CATEGORY_DESCRIPTION_I18N")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, include = "all", region = "com.despegar.sobek.model.BenefitCategoryDescriptionI18N")
public class BenefitCategoryDescriptionI18N
    extends AbstractDescriptionI18N {

    private static final long serialVersionUID = 1L;

}
