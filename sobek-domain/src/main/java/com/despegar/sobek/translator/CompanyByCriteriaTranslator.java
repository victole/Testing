package com.despegar.sobek.translator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.despegar.sobek.dto.CompanySearchResultDTO;

public class CompanyByCriteriaTranslator {

    public List<CompanySearchResultDTO> getCompanySearchResultList(List<Object[]> results) {
        List<CompanySearchResultDTO> companies = new ArrayList<CompanySearchResultDTO>();
        CompanySearchResultDTO companySearchResultDTO;
        for (Object[] tuple : results) {
            companySearchResultDTO = new CompanySearchResultDTO();
            companySearchResultDTO.setFirm((String) tuple[0]);
            companySearchResultDTO.setName((String) tuple[1]);
            companySearchResultDTO.setCompanyOID((Long) tuple[2]);
            companySearchResultDTO.setCreationDate(((Date) tuple[3]).getTime());
            companies.add(companySearchResultDTO);
        }
        return companies;
    }

}
