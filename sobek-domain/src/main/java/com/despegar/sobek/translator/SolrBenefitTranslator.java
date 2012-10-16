package com.despegar.sobek.translator;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanPredicate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.functors.EqualPredicate;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import com.despegar.sobek.dto.BenefitContainerDTO;
import com.despegar.sobek.dto.BenefitDTO;
import com.despegar.sobek.dto.BenefitInformationDTO;
import com.despegar.sobek.utility.SolrFieldHelper;
import com.google.common.collect.Lists;

public class SolrBenefitTranslator {

    private static Logger logger = Logger.getLogger(SolrBenefitTranslator.class);

    private PictureTranslator pictureTranslator;

    public BenefitContainerDTO buildBenefitList(QueryResponse queryResponse) {
        BenefitContainerDTO benefitContainerDTO = new BenefitContainerDTO();
        Lists.newLinkedList();
        Integer countElement = 0;

        logger.info("Building benefit list from Solr query response");
        BenefitDTO benefitDTO = new BenefitDTO();
        List<BenefitDTO> benefits = new LinkedList<BenefitDTO>();

        if (queryResponse.getGroupResponse() != null) {

            for (GroupCommand documentGroupResponse : queryResponse.getGroupResponse().getValues()) {

                for (Group group : documentGroupResponse.getValues()) {

                    SolrDocumentList documentList = group.getResult();
                    for (SolrDocument solrDocument : documentList) {

                        EqualPredicate benefitOIDPredicate = new EqualPredicate(new Long(solrDocument.getFieldValue(
                            "oidBenefit").toString()));
                        BeanPredicate benefitBeanPredicate = new BeanPredicate("OID", benefitOIDPredicate);
                        benefitDTO = (BenefitDTO) CollectionUtils.find(benefits, benefitBeanPredicate);

                        if (benefitDTO == null) {

                            benefitDTO = new BenefitDTO();
                            benefitDTO.setBenefitStatusCode(SolrFieldHelper.getStringIfNotNull(solrDocument, "state"));
                            benefitDTO.setCategoryCode(SolrFieldHelper.getStringIfNotNull(solrDocument, "category"));
                            benefitDTO.setCompanyOID(SolrFieldHelper.getLongIfNotNull(solrDocument, "company"));

                            // TODO: Cambiar el tipo de dato del atributo a Date en el DTO. No usar mas fechas en long,
                            // pasar directamente el Date
                            if (solrDocument.getFieldValue("dateFrom") != null) {
                                Date dateFrom = (Date) solrDocument.getFieldValue("dateFrom");
                                benefitDTO.setDateFrom(dateFrom.getTime());
                            }
                            if (solrDocument.getFieldValue("dateTo") != null) {
                                Date dateTo = (Date) solrDocument.getFieldValue("dateTo");
                                benefitDTO.setDateTo(dateTo.getTime());
                            }

                            benefitDTO.setIsFree(SolrFieldHelper.getBooleanIfNotNull(solrDocument, "isFree"));
                            benefitDTO.setIsOutstanding(SolrFieldHelper.getBooleanIfNotNull(solrDocument, "isOutstanding"));
                            benefitDTO.setOID(SolrFieldHelper.getLongIfNotNull(solrDocument, "oidBenefit"));
                            benefitDTO.setRelevance(SolrFieldHelper.getBigDecimalIfNotNull(solrDocument, "relevance"));

                            String pictureName = SolrFieldHelper.getStringIfNotNull(solrDocument, "companyPicture");
                            if (pictureName != null) {
                                benefitDTO.setPicture(this.pictureTranslator.fillPictureDTO(pictureName.toString()));
                            }

                            benefitDTO.setLinkTemplateType(SolrFieldHelper.getStringIfNotNull(solrDocument,
                                "linkTemplateType"));
                            benefitDTO.getExternalResources().put("PT",
                                SolrFieldHelper.getStringIfNotNull(solrDocument, "linkVoucherPT"));
                            benefitDTO.getExternalResources().put("ES",
                                SolrFieldHelper.getStringIfNotNull(solrDocument, "linkVoucherES"));
                            benefitDTO.setBenefitInformation(this.getBenefitInformation(solrDocument));
                            benefits.add(benefitDTO);
                        }
                    }
                }
            }
            countElement = queryResponse.getGroupResponse().getValues().get(0).getNGroups();
        }
        benefitContainerDTO.setNumberOfResults(countElement);
        benefitContainerDTO.setBenefitDTOs(benefits);
        return benefitContainerDTO;
    }

    private Map<String, BenefitInformationDTO> getBenefitInformation(SolrDocument solrDocument) {

        Map<String, BenefitInformationDTO> benefitInformationMap = new HashMap<String, BenefitInformationDTO>();
        BenefitInformationDTO benefitInformationDTO_ES = new BenefitInformationDTO();
        BenefitInformationDTO benefitInformationDTO_PT = new BenefitInformationDTO();

        benefitInformationDTO_ES.setTitle(SolrFieldHelper.getStringIfNotNull(solrDocument, "titleES"));
        benefitInformationDTO_ES.setBranches(SolrFieldHelper.getStringIfNotNull(solrDocument, "branchesES"));
        benefitInformationDTO_ES.setLink(SolrFieldHelper.getStringIfNotNull(solrDocument, "linkES"));
        benefitInformationDTO_ES.setLinkTitle(SolrFieldHelper.getStringIfNotNull(solrDocument, "linkTitleES"));
        benefitInformationDTO_ES.setTermsAndConditions(SolrFieldHelper.getStringIfNotNull(solrDocument,
            "termsAndConditionsES"));
        benefitInformationDTO_ES.setDescription(SolrFieldHelper.getStringIfNotNull(solrDocument, "descriptionES"));
        benefitInformationMap.put("ES", benefitInformationDTO_ES);

        benefitInformationDTO_PT.setTitle(SolrFieldHelper.getStringIfNotNull(solrDocument, "titlePT"));
        benefitInformationDTO_PT.setBranches(SolrFieldHelper.getStringIfNotNull(solrDocument, "branchesPT"));
        benefitInformationDTO_PT.setLink(SolrFieldHelper.getStringIfNotNull(solrDocument, "linkPT"));
        benefitInformationDTO_PT.setLinkTitle(SolrFieldHelper.getStringIfNotNull(solrDocument, "linkTitlePT"));
        benefitInformationDTO_PT.setTermsAndConditions(SolrFieldHelper.getStringIfNotNull(solrDocument,
            "termsAndConditionsPT"));
        benefitInformationDTO_PT.setDescription(SolrFieldHelper.getStringIfNotNull(solrDocument, "descriptionPT"));
        benefitInformationMap.put("PT", benefitInformationDTO_PT);

        return benefitInformationMap;
    }

    public void setPictureTranslator(PictureTranslator pictureTranslator) {
        this.pictureTranslator = pictureTranslator;
    }

}
