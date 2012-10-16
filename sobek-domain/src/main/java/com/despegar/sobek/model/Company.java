package com.despegar.sobek.model;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.despegar.framework.persistence.AbstractIdentifiablePersistentObjectWithLogicalDeletion;

@Entity
@Table(name = "COMPANY")
public class Company
    extends AbstractIdentifiablePersistentObjectWithLogicalDeletion {

    private static final long serialVersionUID = -5023669946288152868L;

    private String firm;
    private String name;
    private String address;
    private String websiteURL;
    private String description;
    private List<Contact> contacts;
    private Picture picture;
    private Date creationDate;

    @Column(name = "FIRM", nullable = false)
    public String getFirm() {
        return this.firm;
    }

    public void setFirm(String firm) {
        this.firm = firm;
    }

    @Column(name = "ADDRESS")
    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Column(name = "NAME", nullable = false)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "WEBSITE_URL")
    public String getWebsiteURL() {
        return this.websiteURL;
    }

    public void setWebsiteURL(String websiteURL) {
        this.websiteURL = websiteURL;
    }

    @OneToOne
    @JoinColumn(name = "PICTURE_OID")
    @Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    public Picture getPicture() {
        return this.picture;
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }

    @OneToMany(fetch = FetchType.LAZY)
    @Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "COMPANY_OID", nullable = false)
    @ForeignKey(name = "FK_CONTACT__COMPANY")
    @Index(name = "IDX_CONTACT__COMPANY_OID")
    public List<Contact> getContacts() {
        if (this.contacts == null) {
            this.contacts = new LinkedList<Contact>();
        }
        return this.contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    @Column(name = "DESCRIPTION", length = 1000, nullable = true, columnDefinition = "nvarchar(4000)")
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "CREATION_DATE")
    public Date getCreationDate() {
        return this.creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public void addContact(Contact contact) {
        this.getContacts().add(contact);

    }
}
