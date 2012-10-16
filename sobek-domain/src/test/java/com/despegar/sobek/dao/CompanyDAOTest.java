package com.despegar.sobek.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import com.despegar.framework.persistence.hibernate.dao.generic.query.OrderType;
import com.despegar.framework.tests.spring.AbstractTransactionalSpringTest;
import com.despegar.framework.utils.string.StringUtils;
import com.despegar.sobek.model.Company;
import com.despegar.sobek.model.Contact;

@ContextConfiguration(locations = {"classpath:com/despegar/test/test-reference-data-context.xml"})
public class CompanyDAOTest
    extends AbstractTransactionalSpringTest {

    @Resource
    private CompanyDAO companyDAO;

    private Company company1;
    private Company company2;
    private Company company3;
    private Company company4;
    private Contact contact;

    @Before
    public void setUp() {
        this.company1 = new Company();
        this.company1.setAddress("1 Infinite Loop");
        this.company1.setCreationDate(new Date());
        this.company1.setDescription("Descripcion");
        this.company1.setFirm("Apple");
        this.company1.setName("Apple");
        this.company1.setWebsiteURL("www.apple.com");

        this.contact = new Contact();
        this.contact.setCellphone("1111111111");
        this.contact.setEmail("info@apple.com");
        this.contact.setName("Contacto Apple");
        this.contact.setPhone("44444444");
        this.contact.setPosition("Contacto");
        this.contact.setSkype("contacto.apple");

        this.company1.addContact(this.contact);
        this.companyDAO.save(this.company1);

        this.company2 = new Company();
        this.company2.setAddress("11 de Abril 461");
        this.company2.setCreationDate(new Date());
        this.company2.setDescription("Universidad Tecnologica Nacional Facultad Regional Bahia Blanca");
        this.company2.setFirm("UTN");
        this.company2.setName("UTN FRBB");
        this.company2.setWebsiteURL("www.frbb.utn.edu.ar");
        this.companyDAO.save(this.company2);

        this.company3 = new Company();
        this.company3.setAddress("Zeballos 1341");
        this.company3.setCreationDate(new Date());
        this.company3.setDescription("Universidad Tecnologica Nacional Facultad Regional Rosario");
        this.company3.setFirm("UTN");
        this.company3.setName("UTN FRRO");
        this.company3.setWebsiteURL("www.frro.utn.edu.ar/");
        this.companyDAO.save(this.company3);

        this.company4 = new Company();
        this.company4.setAddress("Av. Medrano 951");
        this.company4.setCreationDate(new Date());
        this.company4.setDescription("Universidad Tecnologica Nacional Facultad Regional Buenos Aires");
        this.company4.setFirm("UTN");
        this.company4.setName("UTN FRBA");
        this.company4.setWebsiteURL("www.frba.utn.edu.ar");
        this.companyDAO.save(this.company4);
    }

    @Test
    public void getCompanyByOID_correct_returnsCompany() {

        Company retrievedCompany = this.companyDAO.getCompanyByOID(this.company1.getOID());

        assertEquals(this.company1.getAddress(), retrievedCompany.getAddress());
        assertEquals(this.company1.getCreationDate(), retrievedCompany.getCreationDate());
        assertEquals(this.company1.getDescription(), retrievedCompany.getDescription());
        assertEquals(this.company1.getFirm(), retrievedCompany.getFirm());
        assertEquals(this.company1.getName(), retrievedCompany.getName());
        assertEquals(this.company1.getWebsiteURL(), retrievedCompany.getWebsiteURL());
        assertEquals(this.company1.getContacts().size(), retrievedCompany.getContacts().size());
        assertEquals(this.company1.getContacts().get(0).getCellphone(), retrievedCompany.getContacts().get(0).getCellphone());
        assertEquals(this.company1.getContacts().get(0).getEmail(), retrievedCompany.getContacts().get(0).getEmail());
        assertEquals(this.company1.getContacts().get(0).getName(), retrievedCompany.getContacts().get(0).getName());
        assertEquals(this.company1.getContacts().get(0).getPhone(), retrievedCompany.getContacts().get(0).getPhone());
        assertEquals(this.company1.getContacts().get(0).getPosition(), retrievedCompany.getContacts().get(0).getPosition());
        assertEquals(this.company1.getContacts().get(0).getSkype(), retrievedCompany.getContacts().get(0).getSkype());
    }

    @Test
    public void getCompanyByOID_nonExistingOID_returnsNull() {
        Company retrievedCompany = this.companyDAO.getCompanyByOID(-1L);
        assertNull(retrievedCompany);
    }

    @Test
    public void searchCompaniesByCriteria_correct_returnsListOfCompanies() {

        List<Object[]> searchCompaniesByCriteria = this.companyDAO.searchCompaniesByCriteria("frb", 10, 1, "name",
            OrderType.ASC);

        assertEquals(2, searchCompaniesByCriteria.size());
        assertEquals(this.company4.getFirm(), searchCompaniesByCriteria.get(0)[0]);
        assertEquals(this.company4.getName(), searchCompaniesByCriteria.get(0)[1]);
        assertEquals(this.company4.getOID(), searchCompaniesByCriteria.get(0)[2]);
        assertEquals(this.company2.getFirm(), searchCompaniesByCriteria.get(1)[0]);
        assertEquals(this.company2.getName(), searchCompaniesByCriteria.get(1)[1]);
        assertEquals(this.company2.getOID(), searchCompaniesByCriteria.get(1)[2]);
    }

    @Test
    public void searchCompaniesByCriteria_nonExistingName_returnsEmptyList() {
        List<Object[]> searchCompaniesByCriteria = this.companyDAO.searchCompaniesByCriteria("gre", 10, 1, "name",
            OrderType.ASC);
        assertEquals(0, searchCompaniesByCriteria.size());
    }

    @Test
    public void searchCompaniesByCriteria_emptySearchCriteria_returnsEmptyList() {
        List<Object[]> searchCompaniesByCriteria = this.companyDAO.searchCompaniesByCriteria(StringUtils.EMTPY_STRING, 10,
            1, "name", OrderType.ASC);
        assertTrue(!searchCompaniesByCriteria.isEmpty());
    }

    @Test
    public void getAllCompanies_noParams_ReturnCompanyList() {
        Boolean exists = false;
        List<Company> companies = this.companyDAO.getAllCompanies();
        for (Company company : companies) {
            if (company.getName().equals("UTN FRBA")) {
                exists = true;
            }
        }
        TestCase.assertTrue(exists);
        TestCase.assertTrue(companies.size() >= 3);
    }

    public void setCompanyDAO(CompanyDAO companyDAO) {
        this.companyDAO = companyDAO;
    }


}
