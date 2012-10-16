package com.despegar.sobek.translator;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import com.despegar.sobek.dto.BenefitFilterResultDTO;
import com.despegar.sobek.dto.BenefitItemResultDTO;
import com.google.common.collect.Lists;

public class BenefitFilterResultTranslator {

    public BenefitFilterResultDTO translate(QueryResponse queryResponse) {
        new HashMap<Long, BenefitItemResultDTO>();

        BenefitFilterResultDTO benefitFilterResultDTO = new BenefitFilterResultDTO();
        List<BenefitItemResultDTO> benefit = Lists.newLinkedList();
        Integer countElement = 0;
        if (queryResponse != null && queryResponse.getGroupResponse() != null) {
            for (GroupCommand documentGroupResponse : queryResponse.getGroupResponse().getValues()) {
                for (Group group : documentGroupResponse.getValues()) {
                    SolrDocumentList documentList = group.getResult();
                    BenefitItemResultDTO benefitItemResultDTO = new BenefitItemResultDTO();
                    if (documentList.get(0).getFirstValue("state") != null) {
                        benefitItemResultDTO.setBenefitStatusCode(documentList.get(0).getFirstValue("state").toString());
                    }
                    Map<String, String> titles = new HashMap<String, String>();

                    if (documentList.get(0).getFirstValue("titleES") != null) {
                        titles.put("ES", documentList.get(0).getFirstValue("titleES").toString());
                    }
                    if (documentList.get(0).getFirstValue("titlePT") != null) {
                        titles.put("PT", documentList.get(0).getFirstValue("titlePT").toString());
                    }
                    if (!titles.isEmpty()) {
                        benefitItemResultDTO.setTitles(titles);
                    }
                    if (documentList.get(0).getFirstValue("companyName") != null) {
                        benefitItemResultDTO.setCompanyName(documentList.get(0).getFirstValue("companyName").toString());
                    }
                    if (documentList.get(0).getFirstValue("dateFrom") != null) {
                        Date dateFrom = (Date) documentList.get(0).getFirstValue("dateFrom");
                        benefitItemResultDTO.setDateFrom(dateFrom.getTime());
                    }
                    if (documentList.get(0).getFirstValue("dateTo") != null) {
                        Date dateTo = (Date) documentList.get(0).getFirstValue("dateTo");
                        benefitItemResultDTO.setDateTo(dateTo.getTime());
                    }
                    if (documentList.get(0).getFirstValue("publicationDate") != null) {
                        Date publicationDate = (Date) documentList.get(0).getFirstValue("publicationDate");
                        benefitItemResultDTO.setPublicationDate(publicationDate.getTime());
                    }
                    if (documentList.get(0).getFirstValue("isOutstanding") != null) {
                        benefitItemResultDTO.setIsOutstanding((Boolean) documentList.get(0).getFirstValue("isOutstanding"));
                    }
                    if (documentList.get(0).getFirstValue("oidBenefit") != null) {
                        benefitItemResultDTO.setOID(Long.parseLong(documentList.get(0).getFirstValue("oidBenefit")
                            .toString()));
                    }
                    benefit.add(benefitItemResultDTO);

                }
            }
            countElement = queryResponse.getGroupResponse().getValues().get(0).getNGroups();
        }
        benefitFilterResultDTO.setNumberOfResults(countElement);
        benefitFilterResultDTO.setBenefits(benefit);
        return benefitFilterResultDTO;
    }
}
