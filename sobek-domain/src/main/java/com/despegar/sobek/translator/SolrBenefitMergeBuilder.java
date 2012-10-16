package com.despegar.sobek.translator;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import com.despegar.framework.utils.string.StringUtils;
import com.despegar.sobek.dto.BenefitMergeFilterDTO;
import com.google.common.collect.Lists;

public class SolrBenefitMergeBuilder {

    private static final String ES = "ES";
    private static final String PT = "PT";

    private static Logger logger = Logger.getLogger(SolrBenefitMergeBuilder.class);

    public List<BenefitMergeFilterDTO> buildBenefitList(QueryResponse queryResponse) {
        List<BenefitMergeFilterDTO> benefits = Lists.newArrayList();

        if (queryResponse != null && queryResponse.getGroupResponse() != null) {
            for (GroupCommand documentGroupResponse : queryResponse.getGroupResponse().getValues()) {
                for (Group group : documentGroupResponse.getValues()) {

                    SolrDocumentList documentList = group.getResult();
                    BenefitMergeFilterDTO benefitResult = new BenefitMergeFilterDTO();

                    Object oidBenefit = documentList.get(0).getFirstValue("oidBenefit");
                    if (oidBenefit != null) {
                        benefitResult.setOID(Long.parseLong(oidBenefit.toString()));
                    }

                    Object linkVoucherES = documentList.get(0).getFirstValue("linkVoucherES");
                    if (linkVoucherES != null) {
                        benefitResult.getLinks().put(ES, linkVoucherES.toString());
                    }

                    Object linkVoucherPT = documentList.get(0).getFirstValue("linkVoucherPT");
                    if (linkVoucherPT != null) {
                        benefitResult.getLinks().put(PT, linkVoucherPT.toString());
                    }

                    benefits.add(benefitResult);
                }
            }
        }

        logger.info(StringUtils.concat(benefits.size(), " benefits were retrieved"));
        return benefits;
    }
}
