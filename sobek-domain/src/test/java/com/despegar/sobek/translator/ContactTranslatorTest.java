package com.despegar.sobek.translator;

import static junit.framework.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.despegar.sobek.dto.ContactDTO;
import com.despegar.sobek.model.Contact;

public class ContactTranslatorTest {

    private ContactTranslator contactTranslator;

    @Before
    public void setUp() {
        this.contactTranslator = new ContactTranslator();
    }

    @Test
    public void getDTO_complete_returnContactDTO() {
        Contact contact = new Contact();
        contact.setCellphone("1555555555");
        contact.setEmail("contacto@despegar.com");
        contact.setName("Nombre del contacto");
        contact.setPhone("55555555");
        contact.setPosition("Cargo del contacto");
        contact.setSkype("contactoDespegar");

        ContactDTO contactDTO = this.contactTranslator.getDTO(contact);

        assertEquals(contact.getCellphone(), contactDTO.getCellphone());
        assertEquals(contact.getEmail(), contactDTO.getEmail());
        assertEquals(contact.getName(), contactDTO.getName());
        assertEquals(contact.getPhone(), contactDTO.getPhone());
        assertEquals(contact.getPosition(), contactDTO.getPosition());
        assertEquals(contact.getSkype(), contactDTO.getSkype());
    }

    @Test
    public void getPersistentObject_complete_returnContact() {

        ContactDTO contactDTO = new ContactDTO();
        contactDTO.setCellphone("1555555555");
        contactDTO.setEmail("contacto@despegar.com");
        contactDTO.setName("Nombre del contacto");
        contactDTO.setPhone("55555555");
        contactDTO.setPosition("Cargo del contacto");
        contactDTO.setSkype("contactoDespegar");

        Contact contact = this.contactTranslator.getPersistentObject(contactDTO);

        assertEquals(contactDTO.getCellphone(), contact.getCellphone());
        assertEquals(contactDTO.getEmail(), contact.getEmail());
        assertEquals(contactDTO.getName(), contact.getName());
        assertEquals(contactDTO.getPhone(), contact.getPhone());
        assertEquals(contactDTO.getPosition(), contact.getPosition());
        assertEquals(contactDTO.getSkype(), contact.getSkype());
    }
}
