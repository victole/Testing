package com.despegar.sobek.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import com.despegar.framework.persistence.AbstractIdentifiablePersistentObject;

@MappedSuperclass
public abstract class AbstractCatalogue<D extends AbstractDescriptionI18N>
    extends AbstractIdentifiablePersistentObject
    implements Descriptable<D> {

    private static final long serialVersionUID = 1L;
    private String code;

    @Column(name = "CODE")
    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
