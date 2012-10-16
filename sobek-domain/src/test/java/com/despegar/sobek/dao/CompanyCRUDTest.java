package com.despegar.sobek.dao;

import static junit.framework.Assert.assertEquals;

import java.util.Date;

import org.junit.Ignore;
import org.springframework.test.context.ContextConfiguration;

import com.despegar.framework.persistence.test.support.AbstractReadWriteObjectWLDDAOTest;
import com.despegar.sobek.model.Company;


@ContextConfiguration(locations = {"classpath:com/despegar/test/test-reference-data-context.xml"})
@Ignore
public class CompanyCRUDTest
    extends AbstractReadWriteObjectWLDDAOTest<CompanyDAO, Company> {

    @Override
    protected String getBeanName() {
        return "companyDAO";
    }

    @Override
    protected Company createObject() {
        Company company = new Company();
        company.setAddress("1 Infinite Loop");
        company.setDescription("Apple");
        company.setFirm("Apple");
        company.setName("Apple");
        company.setWebsiteURL("www.apple.com");
        company.setCreationDate(new Date());
        return company;
    }

    @Override
    protected void updateObject(Company object) {
        object.setAddress("2 Infinite Loop");
        object.setDescription("Apple 2");
        object.setName("Apple 2");
    }

    @Override
    protected void assertUpdated(Company company) {
        assertEquals("2 Infinite Loop", company.getAddress());
        assertEquals("Apple 2", company.getDescription());
        assertEquals("Apple 2", company.getName());
    }

    @Override
    protected boolean sameObjects(Company o1, Company o2) {
        return o1.getName().equals(o2.getName()) && o1.getFirm().equals(o2.getFirm())
            && o1.getAddress().equals(o2.getAddress()) && o1.getWebsiteURL().equals(o2.getWebsiteURL())
            && o1.getDescription().equals(o2.getDescription());
    }

}
