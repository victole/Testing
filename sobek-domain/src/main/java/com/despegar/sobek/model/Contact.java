package com.despegar.sobek.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.despegar.framework.persistence.AbstractIdentifiablePersistentObject;

@Entity
@Table(name = "CONTACT")
public class Contact
    extends AbstractIdentifiablePersistentObject {

    private static final long serialVersionUID = 4378456876800356528L;

    private String cellphone;
    private String email;
    private String name;
    private String notes;
    private String position;
    private String skype;
    private String phone;

    @Column(name = "CELLPHONE")
    public String getCellphone() {
        return this.cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    @Column(name = "EMAIL")
    public String getEmail() {
        return this.email;
    }

    public void setEmail(String mail) {
        this.email = mail;
    }

    @Column(name = "NAME")
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "NOTES", length = 1000, nullable = true, columnDefinition = "nvarchar(4000)")
    public String getNotes() {
        return this.notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Column(name = "POSITION")
    public String getPosition() {
        return this.position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Column(name = "SKYPE")
    public String getSkype() {
        return this.skype;
    }

    public void setSkype(String skype) {
        this.skype = skype;
    }

    @Column(name = "PHONE")
    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String telephone) {
        this.phone = telephone;
    }

}
