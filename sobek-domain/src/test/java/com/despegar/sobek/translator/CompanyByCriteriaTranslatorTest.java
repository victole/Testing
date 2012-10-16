package com.despegar.sobek.translator;

import static junit.framework.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import com.despegar.sobek.dto.CompanySearchResultDTO;

public class CompanyByCriteriaTranslatorTest {

    private CompanyByCriteriaTranslator companyByCriteriaTranslator;

    @Before
    public void setUp() {
        this.companyByCriteriaTranslator = new CompanyByCriteriaTranslator();
    }

    @Test
    public void getCompanySearchResultList_emptyObjectList_returnEmptyList() {
        List<CompanySearchResultDTO> companySearchResultList = this.companyByCriteriaTranslator
            .getCompanySearchResultList(new ArrayList<Object[]>());
        TestCase.assertTrue(companySearchResultList.isEmpty());
    }

    @Test
    public void getCompanyResultList_completeList_returnListOfCompanySearchResultDTO() {

        Date now = new Date();

        List<Object[]> list = new ArrayList<Object[]>();
        list.add(new Object[] {"Despegar.com.ar", "Despegar Argentina", 1L, now});
        list.add(new Object[] {"Despegamos.com", "Despegamos", 2L, now});

        List<CompanySearchResultDTO> companySearchResultList = this.companyByCriteriaTranslator
            .getCompanySearchResultList(list);
        TestCase.assertTrue(!companySearchResultList.isEmpty());
        assertEquals((String) list.get(0)[0], companySearchResultList.get(0).getFirm());
        assertEquals((String) list.get(0)[1], companySearchResultList.get(0).getName());
        assertEquals(list.get(0)[2], companySearchResultList.get(0).getCompanyOID());
        assertEquals(now.getTime(), companySearchResultList.get(0).getCreationDate().longValue());
        assertEquals((String) list.get(1)[0], companySearchResultList.get(1).getFirm());
        assertEquals((String) list.get(1)[1], companySearchResultList.get(1).getName());
        assertEquals(list.get(1)[2], companySearchResultList.get(1).getCompanyOID());
        assertEquals(now.getTime(), companySearchResultList.get(1).getCreationDate().longValue());
    }
}
