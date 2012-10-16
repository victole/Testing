package com.despegar.sobek.service;

import com.despegar.sobek.dto.BenefitContainerDTO;
import com.despegar.sobek.dto.BenefitDTO;
import com.despegar.sobek.dto.BenefitFilterContainerDTO;
import com.despegar.sobek.dto.BenefitFilterResultDTO;
import com.despegar.sobek.dto.MergePDFsDTO;

public interface BenefitService {

    public Long save(BenefitDTO benefitDTO);

    public void delete(Long OID);

    public BenefitDTO getBenefit(Long OID);

    public BenefitFilterResultDTO searchBenefits(BenefitFilterContainerDTO benefitFilterContainerDTO);

    public BenefitContainerDTO getBenefitsByCustomSearch(BenefitFilterContainerDTO customSearchDTO);

    public byte[] mergePDFs(MergePDFsDTO mergePDFsDTO);

}
