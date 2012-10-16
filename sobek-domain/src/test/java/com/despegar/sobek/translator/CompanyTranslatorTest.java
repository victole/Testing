package com.despegar.sobek.translator;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.despegar.sobek.dto.CompanyDTO;
import com.despegar.sobek.dto.ContactDTO;
import com.despegar.sobek.dto.PictureDTO;
import com.despegar.sobek.model.Company;
import com.despegar.sobek.model.Contact;
import com.despegar.sobek.model.Picture;
import com.despegar.sobek.utility.ModelContentCreator;

public class CompanyTranslatorTest {

    @Mock
    private ContactTranslator contactTranslatorMock;
    @Mock
    private PictureTranslator pictureTranslatorMock;

    private CompanyTranslator instance;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.instance = new CompanyTranslator();
        this.instance.setContactTranslator(this.contactTranslatorMock);
        this.instance.setPictureTranslator(this.pictureTranslatorMock);
    }

    @Test
    public void getPersistentObject_emptyContactList_returnCompany() {
        Date now = new Date();
        CompanyDTO companyDTO = new CompanyDTO();
        companyDTO.setAddress("Corrientes 746");
        companyDTO.setDescription("Oficinas Despegar");
        companyDTO.setFirm("Despegar.com");
        companyDTO.setName("Despegar.com");
        companyDTO.setWebsiteURL("www.despegar.com");
        PictureDTO pictureDTO = new PictureDTO();
        pictureDTO.setFileName("foto.jpg");
        companyDTO.setPicture(pictureDTO);
        companyDTO.setCreationDate(now.getTime());

        Company company = this.instance.getPersistentObject(companyDTO);

        assertEquals(companyDTO.getAddress(), company.getAddress());
        assertEquals(companyDTO.getDescription(), company.getDescription());
        assertEquals(companyDTO.getFirm(), company.getFirm());
        assertEquals(companyDTO.getName(), company.getName());
        assertEquals(companyDTO.getWebsiteURL(), company.getWebsiteURL());
        assertEquals(companyDTO.getCreationDate().longValue(), company.getCreationDate().getTime());
    }

    @Test
    public void getPersistentObject_oneContact_returnCompany() {
        Date now = new Date();
        Contact contact = new Contact();
        contact.setCellphone("1555555555");
        contact.setEmail("contacto@despegar.com");
        contact.setName("Nombre del contacto");
        contact.setPhone("55555555");
        contact.setPosition("Cargo del contacto");
        contact.setSkype("contactoDespegar");

        ContactDTO contactDTO = new ContactDTO();
        contactDTO.setCellphone("1555555555");
        contactDTO.setEmail("contacto@despegar.com");
        contactDTO.setName("Nombre del contacto");
        contactDTO.setPhone("55555555");
        contactDTO.setPosition("Cargo del contacto");
        contactDTO.setSkype("contactoDespegar");

        CompanyDTO companyDTO = new CompanyDTO();
        companyDTO.setAddress("Corrientes 746");
        companyDTO.setDescription("Oficinas Despegar");
        companyDTO.setFirm("Despegar.com");
        companyDTO.setName("Despegar.com");
        companyDTO.setWebsiteURL("www.despegar.com");
        companyDTO.getContacts().add(contactDTO);
        companyDTO.setCreationDate(now.getTime());

        when(this.contactTranslatorMock.getPersistentObject(contactDTO)).thenReturn(contact);

        Company company = this.instance.getPersistentObject(companyDTO);

        assertEquals(companyDTO.getAddress(), company.getAddress());
        assertEquals(companyDTO.getDescription(), company.getDescription());
        assertEquals(companyDTO.getFirm(), company.getFirm());
        assertEquals(companyDTO.getName(), company.getName());
        assertEquals(companyDTO.getWebsiteURL(), company.getWebsiteURL());
        assertEquals(companyDTO.getContacts().size(), company.getContacts().size());
        assertEquals(companyDTO.getCreationDate().longValue(), company.getCreationDate().getTime());
    }

    @Test
    public void getDTO_emptyContactList_returnCompanyDTO() {
        Date now = new Date();
        Company company = new Company();
        company.setAddress("Corrientes 746");
        company.setDescription("Oficinas Despegar");
        company.setFirm("Despegar.com");
        company.setName("Despegar.com");
        company.setWebsiteURL("www.despegar.com");
        company.setCreationDate(now);
        Picture picture = new Picture();
        picture.setResourceName("nombreDeLaFoto.jpg");
        company.setPicture(picture);

        CompanyDTO companyDTO = this.instance.getDTO(company);

        this.assertCompany(company, companyDTO);
    }

    @Test
    public void getDTO_pictureSizeGeneration_returnCompanyDTO() {
        Date now = new Date();
        Company company = ModelContentCreator.createCompany();
        company.setAddress("Corrientes 746");
        company.setDescription("Oficinas Despegar");
        company.setFirm("Despegar.com");
        company.setName("Despegar.com");
        company.setWebsiteURL("www.despegar.com");
        company.setCreationDate(now);
        Picture picture = new Picture();
        picture.setResourceName("nombreDeLaFoto.jpg");
        company.setPicture(picture);

        PictureDTO pictureDTO = new PictureDTO();
        when(this.pictureTranslatorMock.getDTO(any(Picture.class))).thenReturn(pictureDTO);

        CompanyDTO companyDTO = this.instance.getDTO(company);

        this.assertCompany(company, companyDTO);
        assertNotNull(companyDTO.getPicture());
    }

    @Test
    public void getDTO_nullPicture_returnCompanyDTO() {
        Date now = new Date();
        Company company = ModelContentCreator.createCompany();
        company.setAddress("Corrientes 746");
        company.setDescription("Oficinas Despegar");
        company.setFirm("Despegar.com");
        company.setName("Despegar.com");
        company.setWebsiteURL("www.despegar.com");
        company.setCreationDate(now);
        company.setPicture(null);

        CompanyDTO companyDTO = this.instance.getDTO(company);

        this.assertCompany(company, companyDTO);
        assertNull(companyDTO.getPicture());
    }

    @Test
    public void getDTO_oneContact_returnCompanyDTO() {
        Date now = new Date();
        Company company = new Company();
        company.setAddress("Corrientes 746");
        company.setDescription("Oficinas Despegar");
        company.setFirm("Despegar.com");
        company.setName("Despegar.com");
        company.setWebsiteURL("www.despegar.com");
        company.setCreationDate(now);
        Picture picture = new Picture();
        picture.setResourceName("nombreDeLaFoto.jpg");
        company.setPicture(picture);
        Contact contact = new Contact();
        contact.setCellphone("1555555555");
        contact.setEmail("contacto@despegar.com");
        contact.setName("Nombre del contacto");
        contact.setPhone("55555555");
        contact.setPosition("Cargo del contacto");
        contact.setSkype("contactoDespegar");
        company.getContacts().add(contact);

        ContactDTO contactDTO = new ContactDTO();
        contactDTO.setCellphone("1555555555");
        contactDTO.setEmail("contacto@despegar.com");
        contactDTO.setName("Nombre del contacto");
        contactDTO.setPhone("55555555");
        contactDTO.setPosition("Cargo del contacto");
        contactDTO.setSkype("contactoDespegar");

        when(this.contactTranslatorMock.getDTO(contact)).thenReturn(contactDTO);

        CompanyDTO companyDTO = this.instance.getDTO(company);

        this.assertCompany(company, companyDTO);
    }

    private void assertCompany(Company company, CompanyDTO companyDTO) {
        assertEquals(company.getName(), companyDTO.getName());
        assertEquals(company.getAddress(), companyDTO.getAddress());
        assertEquals(company.getContacts().size(), companyDTO.getContacts().size());
        assertEquals(company.getDescription(), companyDTO.getDescription());
        assertEquals(company.getFirm(), companyDTO.getFirm());
        assertEquals(company.getWebsiteURL(), companyDTO.getWebsiteURL());
        assertEquals(company.getCreationDate().getTime(), companyDTO.getCreationDate().longValue());

    }

}
