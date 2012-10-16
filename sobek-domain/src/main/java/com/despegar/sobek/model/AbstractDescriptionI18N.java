package com.despegar.sobek.model;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import com.despegar.framework.persistence.AbstractIdentifiablePersistentObject;

@MappedSuperclass
public abstract class AbstractDescriptionI18N
    extends AbstractIdentifiablePersistentObject {

    private static final long serialVersionUID = 1L;
    private String description;
    private Language language;

    @Column(name = "DESCRIPTION", length = 1000, nullable = true, columnDefinition = "nvarchar(4000)")
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "LANGUAGE_OID", nullable = false)
    public Language getLanguage() {
        return this.language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }
}
