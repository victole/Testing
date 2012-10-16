package com.despegar.sobek.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "BENEFIT_DESCRIPTION_I18N")
public class BenefitDescriptionI18N
    extends AbstractDescriptionI18N {

    private static final long serialVersionUID = 1L;
    private String branches;
    private String termsAndConditions;
    private String title;
    private String link;
    private String linkTitle;

    @Column(name = "BRANCHES", length = 1000, nullable = true, columnDefinition = "nvarchar(4000)")
    public String getBranches() {
        return this.branches;
    }

    public void setBranches(String branches) {
        this.branches = branches;
    }

    @Column(name = "TERMS_AND_CONDITIONS", length = 1000, nullable = true, columnDefinition = "nvarchar(4000)")
    public String getTermsAndConditions() {
        return this.termsAndConditions;
    }

    public void setTermsAndConditions(String termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
    }

    @Column(name = "TITLE")
    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "LINK")
    public String getLink() {
        return this.link;
    }

    public void setLink(String url) {
        this.link = url;
    }

    @Column(name = "LINK_TITLE")
    public String getLinkTitle() {
        return this.linkTitle;
    }

    public void setLinkTitle(String urlTitle) {
        this.linkTitle = urlTitle;
    }
}
