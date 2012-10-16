package com.despegar.sobek.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.despegar.framework.persistence.AbstractIdentifiablePersistentObject;

@SuppressWarnings("serial")
@Entity
@Table(name = "LANGUAGE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, include = "all", region = "com.despegar.sobek.model.Language")
public class Language
    extends AbstractIdentifiablePersistentObject {

    private String isoCode;
    private String name;

    @Column(name = "ISO_CODE", nullable = false, unique = true, columnDefinition = "varchar(3)")
    public String getIsoCode() {
        return this.isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    @Column(name = "NAME")
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
