package com.despegar.sobek.service;

import java.util.List;

import com.despegar.sobek.dto.CompanyDTO;
import com.despegar.sobek.dto.CompanyFilterDTO;
import com.despegar.sobek.dto.CompanyIdentifierDTO;
import com.despegar.sobek.dto.CompanySearchResultContainerDTO;

public interface CompanyService {

    /**
     * Returns a Company by its oid
     * 
     * @param companyOID
     * @return {@link#CompanyDTO} a dto containing the information of the company
     */
    public CompanyDTO getCompanyByOID(Long companyOID);

    /**
     * Saves a company
     * 
     * @param companyDTO
     * @return the OID of the saved company
     */
    public Long save(CompanyDTO companyDTO);

    /**
     * Gets a list of companies for the given search criteria order by a specific field
     * 
     * @param companyFilterDTO
     * @return
     */
    public CompanySearchResultContainerDTO searchCompaniesByCriteria(CompanyFilterDTO companyFilterDTO);

    /**
     * Deletes the company for the given OID
     * 
     * @param companyOID
     */
    public void deleteCompany(Long companyOID);

    /**
     * Get All the companies No params need
     */
    public List<CompanyIdentifierDTO> getAllCompanies();
}
