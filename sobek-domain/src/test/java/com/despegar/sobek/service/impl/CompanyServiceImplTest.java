package com.despegar.sobek.service.impl;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.despegar.framework.persistence.hibernate.dao.generic.query.OrderType;
import com.despegar.library.rest.connector.exceptions.ServiceException;
import com.despegar.framework.utils.string.StringUtils;
import com.despegar.sobek.dao.BenefitDAO;
import com.despegar.sobek.dao.CompanyDAO;
import com.despegar.sobek.dto.CompanyDTO;
import com.despegar.sobek.dto.CompanyFilterDTO;
import com.despegar.sobek.dto.CompanyIdentifierDTO;
import com.despegar.sobek.dto.CompanySearchResultContainerDTO;
import com.despegar.sobek.dto.CompanySearchResultDTO;
import com.despegar.sobek.dto.ContactDTO;
import com.despegar.sobek.dto.PictureDTO;
import com.despegar.sobek.dto.CompanyFilterDTO.OrderByType;
import com.despegar.sobek.dto.CompanyFilterDTO.OrderDirectionType;
import com.despegar.sobek.exception.SobekServiceException;
import com.despegar.sobek.model.Company;
import com.despegar.sobek.model.Contact;
import com.despegar.sobek.model.Picture;
import com.despegar.sobek.solr.index.manager.BenefitIndexManager;
import com.despegar.sobek.translator.CompanyByCriteriaTranslator;
import com.despegar.sobek.translator.CompanyIdentifierTranslator;
import com.despegar.sobek.translator.CompanyTranslator;
import com.google.common.collect.Lists;

public class CompanyServiceImplTest {

    private CompanyServiceImpl instance = new CompanyServiceImpl();
    @Mock
    private CompanyDAO companyDAOMock;
    @Mock
    private CompanyTranslator companyTranslatorMock;

    @Mock
    private CompanyIdentifierTranslator companyIdentifierTranslatorMock;
    @Mock
    private CompanyByCriteriaTranslator companyByCriteriaTranslator;

    @Mock
    private BenefitDAO benefitDAOMock;

    @Mock
    private BenefitIndexManager benefitIndexMock;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.instance.setCompanyDAO(this.companyDAOMock);
        this.instance.setCompanyTranslator(this.companyTranslatorMock);
        this.instance.setCompanyIdentifierTranslator(this.companyIdentifierTranslatorMock);
        this.instance.setCompanyByCriteriaTranslator(this.companyByCriteriaTranslator);
        this.instance.setBenefitDAO(this.benefitDAOMock);
        this.instance.setBenefitIndexManager(this.benefitIndexMock);

        when(this.benefitDAOMock.getBenefitOIDsByCompanyOID(any(Long.class))).thenReturn(null);
        Mockito.doNothing().when(this.benefitIndexMock).update(Mockito.anyListOf(Long.class));
        Mockito.doNothing().when(this.benefitIndexMock).delete(Mockito.anyListOf(Long.class));

        Map<String, String> mapCompanySortingValues = new HashMap<String, String>();
        mapCompanySortingValues.put("CREATION_DATE", "creationDate");
        mapCompanySortingValues.put("FIRM", "firm");
        mapCompanySortingValues.put("NAME", "name");
        this.instance.setMapCompanySortingValues(mapCompanySortingValues);
    }

    @Test
    public void getCompanyByOID_correct_returnsCompanyDTO() {

        Long companyOID = -1L;

        Company company = new Company();
        company.setAddress("Corrientes 746");
        company.setDescription("Oficinas Despegar");
        company.setFirm("Despegar.com");
        company.setName("Despegar.com");
        company.setWebsiteURL("www.despegar.com");
        Contact contact = new Contact();
        contact.setCellphone("1555555555");
        contact.setEmail("contacto@despegar.com");
        contact.setName("Nombre del contacto");
        contact.setPhone("55555555");
        contact.setPosition("Cargo del contacto");
        contact.setSkype("contactoDespegar");
        company.getContacts().add(contact);

        CompanyDTO companyDTO = new CompanyDTO();
        companyDTO.setAddress("Corrientes 746");
        companyDTO.setDescription("Oficinas Despegar");
        companyDTO.setFirm("Despegar.com");
        companyDTO.setName("Despegar.com");
        companyDTO.setWebsiteURL("www.despegar.com");
        ContactDTO contactDTO = new ContactDTO();
        contactDTO.setCellphone("1555555555");
        contactDTO.setEmail("contacto@despegar.com");
        contactDTO.setName("Nombre del contacto");
        contactDTO.setPhone("55555555");
        contactDTO.setPosition("Cargo del contacto");
        contactDTO.setSkype("contactoDespegar");
        companyDTO.getContacts().add(contactDTO);

        when(this.companyDAOMock.getCompanyByOID(companyOID)).thenReturn(company);
        when(this.companyTranslatorMock.getDTO(company)).thenReturn(companyDTO);

        CompanyDTO retrievedCompany = this.instance.getCompanyByOID(companyOID);

        verify(this.companyTranslatorMock).getDTO(company);
        verify(this.companyDAOMock).getCompanyByOID(companyOID);

        assertNotNull(retrievedCompany);
        assertEquals(company.getName(), retrievedCompany.getName());
        assertEquals(company.getAddress(), retrievedCompany.getAddress());
        assertEquals(company.getFirm(), retrievedCompany.getFirm());
        assertEquals(company.getDescription(), retrievedCompany.getDescription());
        assertEquals(company.getWebsiteURL(), retrievedCompany.getWebsiteURL());
        assertEquals(company.getContacts().size(), retrievedCompany.getContacts().size());
        ContactDTO retrievedContact = retrievedCompany.getContacts().get(0);
        assertEquals(contact.getCellphone(), retrievedContact.getCellphone());
        assertEquals(contact.getEmail(), retrievedContact.getEmail());
        assertEquals(contact.getName(), retrievedContact.getName());
        assertEquals(contact.getPhone(), retrievedContact.getPhone());
        assertEquals(contact.getPosition(), retrievedContact.getPosition());
        assertEquals(contact.getSkype(), retrievedContact.getSkype());
    }

    @Test(expected = ServiceException.class)
    public void getCompanyByOID_nullOID_throwsServiceException() {

        when(this.companyDAOMock.getCompanyByOID(any(Long.class))).thenReturn(null);

        try {
            this.instance.getCompanyByOID(null);
        } catch (ServiceException se) {
            verify(this.companyTranslatorMock, never()).getDTO(any(Company.class));
            verify(this.companyDAOMock, never()).getCompanyByOID(any(Long.class));
            throw se;
        }
    }

    @Test(expected = SobekServiceException.class)
    public void getCompanyByOID_nonExistingOID_throwsServiceException() {

        when(this.companyDAOMock.getCompanyByOID(any(Long.class))).thenReturn(null);

        try {
            this.instance.getCompanyByOID(9999L);
        } catch (ServiceException se) {
            verify(this.companyTranslatorMock, never()).getDTO(any(Company.class));
            verify(this.companyDAOMock, never()).getCompanyByOID(any(Long.class));
            throw se;
        }
    }

    @Test
    public void getAllCompanies_noParams_Correct() {
        Long companyOID = -1L;
        Company company = new Company();
        company.setFirm("Despegar.com");
        company.setName("Despegar.com");
        company.setOID(companyOID);

        companyOID = -2L;
        Company company2 = new Company();
        company2.setFirm("Despegar2");
        company2.setName("Despegar2");
        company2.setOID(companyOID);

        CompanyIdentifierDTO companyIdentifierDTO = new CompanyIdentifierDTO();
        companyIdentifierDTO.setFirm("Despegar.com");
        companyIdentifierDTO.setName("Despegar.com");
        companyIdentifierDTO.setOID(-1L);

        CompanyIdentifierDTO companyIdentifierDTO2 = new CompanyIdentifierDTO();
        companyIdentifierDTO2.setFirm("Despegar.com");
        companyIdentifierDTO2.setName("Despegar.com");
        companyIdentifierDTO2.setOID(-2L);

        List<Company> companies = new ArrayList<Company>();
        companies.add(company);
        companies.add(company2);

        when(this.companyIdentifierTranslatorMock.getDTO(company)).thenReturn(companyIdentifierDTO);
        when(this.companyIdentifierTranslatorMock.getDTO(company2)).thenReturn(companyIdentifierDTO2);
        when(this.companyDAOMock.getAllCompanies()).thenReturn(companies);

        List<CompanyIdentifierDTO> companyIdentifierDTOs = this.instance.getAllCompanies();

        verify(this.companyIdentifierTranslatorMock, Mockito.times(2)).getDTO(any(Company.class));
        TestCase.assertTrue(companyIdentifierDTOs.size() == 2);
        TestCase.assertEquals(companyIdentifierDTOs.get(0).getName(), company.getName());
    }

    @Test
    public void save_correct_returnsNewCompanyOID() {
        Long companyOID = -1L;
        Company company = new Company();
        company.setAddress("Corrientes 746");
        company.setDescription("Oficinas Despegar");
        company.setFirm("Despegar.com");
        company.setName("Despegar.com");
        company.setWebsiteURL("www.despegar.com");
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
        company.setOID(companyOID);

        PictureDTO pictureDTO = new PictureDTO();
        pictureDTO.setFileName("nombreDeLaFoto.jpg");
        CompanyDTO companyDTO = new CompanyDTO();
        companyDTO.setAddress("Corrientes 746");
        companyDTO.setDescription("Oficinas Despegar");
        companyDTO.setFirm("Despegar.com");
        companyDTO.setName("Despegar.com");
        companyDTO.setWebsiteURL("www.despegar.com");
        companyDTO.setPicture(pictureDTO);
        ContactDTO contactDTO = new ContactDTO();
        contactDTO.setCellphone("1555555555");
        contactDTO.setEmail("contacto@despegar.com");
        contactDTO.setName("Nombre del contacto");
        contactDTO.setPhone("55555555");
        contactDTO.setPosition("Cargo del contacto");
        contactDTO.setSkype("contactoDespegar");
        companyDTO.getContacts().add(contactDTO);

        when(this.companyTranslatorMock.getPersistentObject(companyDTO)).thenReturn(company);

        Long savedCompanyOID = this.instance.save(companyDTO);

        assertNotNull(savedCompanyOID);
        assertEquals(companyOID, savedCompanyOID);
    }

    @Test(expected = ServiceException.class)
    public void save_NoFirm_throwsServiceException() {
        CompanyDTO companyDTO = new CompanyDTO();
        companyDTO.setAddress("Corrientes 746");
        companyDTO.setDescription("Oficinas Despegar");
        companyDTO.setName("Despegar.com");
        companyDTO.setWebsiteURL("www.despegar.com");

        try {
            this.instance.save(companyDTO);
        } catch (ServiceException se) {
            verify(this.companyTranslatorMock, never()).getPersistentObject(any(CompanyDTO.class));
            verify(this.companyDAOMock, never()).save(any(Company.class));
            throw se;
        }
    }

    @Test(expected = ServiceException.class)
    public void save_NoName_throwsServiceException() {
        CompanyDTO companyDTO = new CompanyDTO();
        companyDTO.setAddress("Corrientes 746");
        companyDTO.setDescription("Oficinas Despegar");
        companyDTO.setFirm("Despegar.com");
        companyDTO.setWebsiteURL("www.despegar.com");

        try {
            this.instance.save(companyDTO);
        } catch (ServiceException se) {
            verify(this.companyTranslatorMock, never()).getPersistentObject(any(CompanyDTO.class));
            verify(this.companyDAOMock, never()).save(any(Company.class));
            throw se;
        }
    }

    @Test
    public void searchCompaniesByCriteria_correct_returnsCompanySearchResultDTOs() {
        Date now = new Date();

        List<Object[]> results = Lists.newArrayList();
        results.add(new Object[] {"Despegar.com.ar", "Despegar Argentina", 1L, now});
        results.add(new Object[] {"Despegamos.com", "Despegamos", 2L, now});

        List<CompanySearchResultDTO> companies = Lists.newArrayList();
        CompanySearchResultDTO companySearchResultDTO = new CompanySearchResultDTO();
        companySearchResultDTO.setFirm("Despegar.com.ar");
        companySearchResultDTO.setName("Despegar Argentina");
        companySearchResultDTO.setCompanyOID(1L);
        companySearchResultDTO.setCreationDate(now.getTime());
        companies.add(companySearchResultDTO);
        companySearchResultDTO = new CompanySearchResultDTO();
        companySearchResultDTO.setFirm("Despegamos.com");
        companySearchResultDTO.setName("Despegamos");
        companySearchResultDTO.setCompanyOID(2L);
        companySearchResultDTO.setCreationDate(now.getTime());
        companies.add(companySearchResultDTO);

        CompanyFilterDTO companyFilterDTO = new CompanyFilterDTO();
        companyFilterDTO.setOrderBy(OrderByType.NAME);
        companyFilterDTO.setOrderDirection(OrderDirectionType.ASC);
        companyFilterDTO.setPageSize(10);
        companyFilterDTO.setPageNumber(1);
        companyFilterDTO.setSearchCriteria("des");

        when(this.companyDAOMock.searchCompaniesByCriteria("des", 10, 1, "name", OrderType.ASC)).thenReturn(results);
        when(this.companyDAOMock.countCompaniesByCriteria("des")).thenReturn(2);
        when(this.companyByCriteriaTranslator.getCompanySearchResultList(results)).thenReturn(companies);

        CompanySearchResultContainerDTO companiesSearchResult = this.instance.searchCompaniesByCriteria(companyFilterDTO);

        verify(this.companyByCriteriaTranslator).getCompanySearchResultList(results);
        verify(this.companyDAOMock).searchCompaniesByCriteria("des", 10, 1, "name", OrderType.ASC);

        List<CompanySearchResultDTO> companiesByCriteria = companiesSearchResult.getCompanies();

        assertNotNull(companiesByCriteria);
        assertEquals(2, companiesByCriteria.size());
        assertEquals(2, companiesSearchResult.getNumberOfResults().intValue());
        assertEquals(results.get(0)[0], companiesByCriteria.get(0).getFirm());
        assertEquals(results.get(0)[1], companiesByCriteria.get(0).getName());
        assertEquals(results.get(0)[2], companiesByCriteria.get(0).getCompanyOID());
        Long dateEpoch = ((Date) results.get(0)[3]).getTime();
        assertEquals(dateEpoch, companiesByCriteria.get(0).getCreationDate());
        assertEquals(results.get(1)[0], companiesByCriteria.get(1).getFirm());
        assertEquals(results.get(1)[1], companiesByCriteria.get(1).getName());
        assertEquals(results.get(1)[2], companiesByCriteria.get(1).getCompanyOID());
        dateEpoch = ((Date) results.get(1)[3]).getTime();
        assertEquals(dateEpoch, companiesByCriteria.get(1).getCreationDate());
    }

    @Test
    public void searchCompaniesByCriteria_correctOrderDesc_returnsCompanySearchResultDTOs() {
        Date now = new Date();

        List<Object[]> results = Lists.newArrayList();
        results.add(new Object[] {"Despegar.com.ar", "Despegar Argentina", 1L, now});
        results.add(new Object[] {"Despegamos.com", "Despegamos", 2L, now});

        List<CompanySearchResultDTO> companies = Lists.newArrayList();
        CompanySearchResultDTO companySearchResultDTO = new CompanySearchResultDTO();
        companySearchResultDTO.setFirm("Despegamos.com");
        companySearchResultDTO.setName("Despegamos");
        companySearchResultDTO.setCompanyOID(2L);
        companySearchResultDTO.setCreationDate(now.getTime());
        companies.add(companySearchResultDTO);
        companySearchResultDTO = new CompanySearchResultDTO();
        companySearchResultDTO.setFirm("Despegar.com.ar");
        companySearchResultDTO.setName("Despegar Argentina");
        companySearchResultDTO.setCompanyOID(1L);
        companySearchResultDTO.setCreationDate(now.getTime());
        companies.add(companySearchResultDTO);

        CompanyFilterDTO companyFilterDTO = new CompanyFilterDTO();
        companyFilterDTO.setOrderBy(OrderByType.NAME);
        companyFilterDTO.setOrderDirection(OrderDirectionType.DESC);
        companyFilterDTO.setPageSize(10);
        companyFilterDTO.setPageNumber(1);
        companyFilterDTO.setSearchCriteria("des");

        when(this.companyDAOMock.searchCompaniesByCriteria("des", 10, 1, "name", OrderType.DES)).thenReturn(results);
        when(this.companyDAOMock.countCompaniesByCriteria("des")).thenReturn(2);
        when(this.companyByCriteriaTranslator.getCompanySearchResultList(results)).thenReturn(companies);

        CompanySearchResultContainerDTO companiesSearchResult = this.instance.searchCompaniesByCriteria(companyFilterDTO);

        verify(this.companyByCriteriaTranslator).getCompanySearchResultList(results);
        verify(this.companyDAOMock).searchCompaniesByCriteria("des", 10, 1, "name", OrderType.DES);

        List<CompanySearchResultDTO> companiesByCriteria = companiesSearchResult.getCompanies();

        assertNotNull(companiesByCriteria);
        assertEquals(2, companiesByCriteria.size());
        assertEquals(results.get(0)[0], companiesByCriteria.get(1).getFirm());
        assertEquals(results.get(0)[1], companiesByCriteria.get(1).getName());
        assertEquals(results.get(0)[2], companiesByCriteria.get(1).getCompanyOID());
        Long dateEpoch = ((Date) results.get(0)[3]).getTime();
        assertEquals(dateEpoch, companiesByCriteria.get(1).getCreationDate());
        assertEquals(2, companiesSearchResult.getNumberOfResults().intValue());
        assertEquals(results.get(1)[0], companiesByCriteria.get(0).getFirm());
        assertEquals(results.get(1)[1], companiesByCriteria.get(0).getName());
        assertEquals(results.get(1)[2], companiesByCriteria.get(0).getCompanyOID());
        dateEpoch = ((Date) results.get(1)[3]).getTime();
        assertEquals(dateEpoch, companiesByCriteria.get(0).getCreationDate());
    }

    @Test
    public void searchCompaniesByCriteria_emptySearchCriteria_returnsCompanySearchResultDTOs() {
        Date now = new Date();

        List<Object[]> results = Lists.newArrayList();
        results.add(new Object[] {"Despegar.com.ar", "Despegar Argentina", 1L, now});
        results.add(new Object[] {"Despegamos.com", "Despegamos", 2L, now});

        List<CompanySearchResultDTO> companies = Lists.newArrayList();
        CompanySearchResultDTO companySearchResultDTO = new CompanySearchResultDTO();
        companySearchResultDTO.setFirm("Despegar.com.ar");
        companySearchResultDTO.setName("Despegar Argentina");
        companySearchResultDTO.setCompanyOID(1L);
        companySearchResultDTO.setCreationDate(now.getTime());
        companies.add(companySearchResultDTO);
        companySearchResultDTO = new CompanySearchResultDTO();
        companySearchResultDTO.setFirm("Despegamos.com");
        companySearchResultDTO.setName("Despegamos");
        companySearchResultDTO.setCompanyOID(2L);
        companySearchResultDTO.setCreationDate(now.getTime());
        companies.add(companySearchResultDTO);

        CompanyFilterDTO companyFilterDTO = new CompanyFilterDTO();
        companyFilterDTO.setOrderBy(OrderByType.NAME);
        companyFilterDTO.setOrderDirection(OrderDirectionType.ASC);
        companyFilterDTO.setPageSize(10);
        companyFilterDTO.setPageNumber(1);
        companyFilterDTO.setSearchCriteria(StringUtils.EMTPY_STRING);

        when(this.companyDAOMock.searchCompaniesByCriteria(StringUtils.EMTPY_STRING, 10, 1, "name", OrderType.ASC))
            .thenReturn(results);
        when(this.companyDAOMock.countCompaniesByCriteria(StringUtils.EMTPY_STRING)).thenReturn(2);
        when(this.companyByCriteriaTranslator.getCompanySearchResultList(results)).thenReturn(companies);

        CompanySearchResultContainerDTO companiesSearchResult = this.instance.searchCompaniesByCriteria(companyFilterDTO);

        verify(this.companyByCriteriaTranslator).getCompanySearchResultList(results);
        verify(this.companyDAOMock).searchCompaniesByCriteria(StringUtils.EMTPY_STRING, 10, 1, "name", OrderType.ASC);

        List<CompanySearchResultDTO> companiesByCriteria = companiesSearchResult.getCompanies();

        assertNotNull(companiesByCriteria);
        assertEquals(2, companiesByCriteria.size());
        assertEquals(2, companiesSearchResult.getNumberOfResults().intValue());
        assertEquals(results.get(0)[0], companiesByCriteria.get(0).getFirm());
        assertEquals(results.get(0)[1], companiesByCriteria.get(0).getName());
        assertEquals(results.get(0)[2], companiesByCriteria.get(0).getCompanyOID());
        Long dateEpoch = ((Date) results.get(0)[3]).getTime();
        assertEquals(dateEpoch, companiesByCriteria.get(0).getCreationDate());
        assertEquals(results.get(1)[0], companiesByCriteria.get(1).getFirm());
        assertEquals(results.get(1)[1], companiesByCriteria.get(1).getName());
        assertEquals(results.get(1)[2], companiesByCriteria.get(1).getCompanyOID());
        dateEpoch = ((Date) results.get(1)[3]).getTime();
        assertEquals(dateEpoch, companiesByCriteria.get(1).getCreationDate());
    }

    @Test
    public void deleteCompany_correct_void() {
        Long companyOID = -1L;
        Company company = new Company();
        company.setAddress("Corrientes 746");
        company.setDescription("Oficinas Despegar");
        company.setFirm("Despegar.com");
        company.setName("Despegar.com");
        company.setWebsiteURL("www.despegar.com");
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
        company.setOID(companyOID);

        when(this.companyDAOMock.read(-1L)).thenReturn(company);

        this.instance.deleteCompany(-1L);

        verify(this.companyDAOMock).read(-1L);
        verify(this.companyDAOMock).deleteWithFlush(company);
    }

    @Test(expected = ServiceException.class)
    public void deleteCompany_nullCompanyOID_throwsServiceException() {
        CompanyDTO companyDTO = new CompanyDTO();
        companyDTO.setAddress("Corrientes 746");
        companyDTO.setDescription("Oficinas Despegar");
        companyDTO.setFirm("Despegar.com");
        companyDTO.setName("Despegar.com");
        companyDTO.setWebsiteURL("www.despegar.com");

        try {
            this.instance.deleteCompany(null);
        } catch (ServiceException se) {
            verify(this.companyDAOMock, never()).delete(any(Company.class));
            verify(this.companyDAOMock, never()).read(any(Long.class));
            throw se;
        }
    }

    @Test(expected = ServiceException.class)
    public void deleteCompany_invalidCompanyOID_throwsServiceException() {
        CompanyDTO companyDTO = new CompanyDTO();
        companyDTO.setAddress("Corrientes 746");
        companyDTO.setDescription("Oficinas Despegar");
        companyDTO.setFirm("Despegar.com");
        companyDTO.setName("Despegar.com");
        companyDTO.setWebsiteURL("www.despegar.com");

        try {
            this.instance.deleteCompany(-1L);
            verify(this.companyDAOMock).read(-1L);
        } catch (ServiceException se) {
            verify(this.companyDAOMock, never()).delete(any(Company.class));
            throw se;
        }
    }
}
