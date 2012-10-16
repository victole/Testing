package com.despegar.sobek.translator;

import java.util.Date;

import com.despegar.framework.mapper.translator.AbstractIdentifiableDTOTranslator;
import com.despegar.sobek.dto.CompanyDTO;
import com.despegar.sobek.dto.ContactDTO;
import com.despegar.sobek.dto.PictureDTO;
import com.despegar.sobek.model.Company;
import com.despegar.sobek.model.Contact;

public class CompanyTranslator
    extends AbstractIdentifiableDTOTranslator<CompanyDTO, Company> {

    private ContactTranslator contactTranslator;
    private PictureTranslator pictureTranslator;

    @Override
    protected Company fillPersistentObject(CompanyDTO dto, Company entity) {
        entity.setAddress(dto.getAddress());
        entity.setDescription(dto.getDescription());
        entity.setFirm(dto.getFirm());
        entity.setName(dto.getName());
        entity.setWebsiteURL(dto.getWebsiteURL());

        PictureDTO pictureDTO = dto.getPicture();
        if (pictureDTO != null && !pictureDTO.getFileName().isEmpty()) {
            entity.setPicture(this.pictureTranslator.getPersistentObject(pictureDTO));
        }

        for (ContactDTO contactDTO : dto.getContacts()) {
            entity.addContact(this.contactTranslator.getPersistentObject(contactDTO));
        }

        entity.setCreationDate(new Date(dto.getCreationDate()));
        entity.setOID(dto.getOID());
        return entity;
    }

    @Override
    protected CompanyDTO fillDTO(CompanyDTO dto, Company persistentObject) {
        dto.setAddress(persistentObject.getAddress());
        dto.setDescription(persistentObject.getDescription());
        dto.setFirm(persistentObject.getFirm());
        dto.setName(persistentObject.getName());

        PictureDTO pictureDTO = this.pictureTranslator.getDTO(persistentObject.getPicture());
        dto.setPicture(pictureDTO);

        for (Contact contact : persistentObject.getContacts()) {
            dto.getContacts().add(this.contactTranslator.getDTO(contact));
        }

        dto.setWebsiteURL(persistentObject.getWebsiteURL());
        dto.setOID(persistentObject.getOID());
        dto.setCreationDate(persistentObject.getCreationDate().getTime());
        return dto;
    }

    public void setContactTranslator(ContactTranslator contactTranslator) {
        this.contactTranslator = contactTranslator;
    }

    public void setPictureTranslator(PictureTranslator pictureTranslator) {
        this.pictureTranslator = pictureTranslator;
    }

}
