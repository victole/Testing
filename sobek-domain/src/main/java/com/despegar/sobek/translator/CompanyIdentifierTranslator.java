package com.despegar.sobek.translator;

import com.despegar.sobek.dto.CompanyIdentifierDTO;
import com.despegar.sobek.model.Company;

public class CompanyIdentifierTranslator {

    public CompanyIdentifierDTO getDTO(Company company) {
        CompanyIdentifierDTO companyIdentifierDTO = new CompanyIdentifierDTO();
        companyIdentifierDTO.setOID(company.getOID());
        companyIdentifierDTO.setVersion(company.getVersion());
        companyIdentifierDTO.setName(company.getName());
        companyIdentifierDTO.setFirm(company.getFirm());
        return companyIdentifierDTO;
    }
}
