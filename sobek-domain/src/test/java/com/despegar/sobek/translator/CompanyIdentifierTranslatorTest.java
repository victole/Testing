package com.despegar.sobek.translator;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import com.despegar.sobek.dto.CompanyIdentifierDTO;
import com.despegar.sobek.model.Company;

public class CompanyIdentifierTranslatorTest {
    private CompanyIdentifierTranslator companyIdentifierTranslator;

    @Before
    public void setUp() {
        this.companyIdentifierTranslator = new CompanyIdentifierTranslator();
    }

    @Test
    public void getDTO_anyCompany_returnCorrect() {
        CompanyIdentifierDTO companyIdentifierDTO = new CompanyIdentifierDTO();
        Company company = new Company();
        company.setName("Despegar.com");
        company.setFirm("Despegar.com");
        company.setOID(-1L);

        companyIdentifierDTO = this.companyIdentifierTranslator.getDTO(company);
        TestCase.assertEquals(companyIdentifierDTO.getName(), company.getName());
        TestCase.assertEquals(companyIdentifierDTO.getFirm(), company.getFirm());
    }
}
