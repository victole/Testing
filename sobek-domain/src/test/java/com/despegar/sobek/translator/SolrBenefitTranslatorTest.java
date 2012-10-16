package com.despegar.sobek.translator;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.despegar.sobek.dto.BenefitContainerDTO;
import com.despegar.sobek.dto.BenefitDTO;
import com.despegar.sobek.dto.BenefitInformationDTO;
import com.despegar.sobek.dto.PictureDTO;

public class SolrBenefitTranslatorTest {

    private QueryResponse queryResponse;
    private Date now;

    private SolrBenefitTranslator instance = new SolrBenefitTranslator();

    @Mock
    private PictureTranslator pictureTranslatorMock;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.instance.setPictureTranslator(this.pictureTranslatorMock);
        this.now = new Date();
        this.queryResponse = this.createQueryResponse(this.now);
    }

    @Test
    public void buildBenefitList_correct_returnsBenefitContainerDTO() {
        this.queryResponse = this.createQueryResponse(this.now);
        PictureDTO pictureDTO = new PictureDTO();
        pictureDTO.setFileName("foto");

        when(this.pictureTranslatorMock.fillPictureDTO("foto")).thenReturn(pictureDTO);

        BenefitContainerDTO buildBenefitList = this.instance.buildBenefitList(this.queryResponse);

        verify(this.pictureTranslatorMock).fillPictureDTO("foto");

        assertEquals(1, buildBenefitList.getBenefitDTOs().size());
        BenefitDTO benefitDTO = buildBenefitList.getBenefitDTOs().get(0);
        assertEquals("state", benefitDTO.getBenefitStatusCode());
        assertEquals(1L, benefitDTO.getCompanyOID().longValue());
        assertEquals(this.now.getTime(), benefitDTO.getDateFrom().longValue());
        assertEquals(this.now.getTime(), benefitDTO.getDateTo().longValue());
        assertEquals(1L, benefitDTO.getOID().longValue());
        assertEquals(true, benefitDTO.getIsOutstanding());
        BenefitInformationDTO informationES = benefitDTO.getBenefitInformation().get("ES");
        BenefitInformationDTO informationPT = benefitDTO.getBenefitInformation().get("PT");
        assertEquals("titleES", informationES.getTitle());
        assertEquals("titlePT", informationPT.getTitle());
        assertEquals(new BigDecimal(1), benefitDTO.getRelevance());
    }

    private QueryResponse createQueryResponse(Date now) {
        QueryResponse queryResponse = new QueryResponse();
        GroupCommand command = new GroupCommand("name", 0);
        SolrDocumentList result = new SolrDocumentList();
        SolrDocument element = new SolrDocument();
        element.setField("state", "state");
        element.setField("titleES", "titleES");
        element.setField("titlePT", "titlePT");
        element.setField("branchesES", "branchesES");
        element.setField("branchesPT", "branchesPT");
        element.setField("linkES", "linkES");
        element.setField("linkPT", "linkPT");
        element.setField("linkTitleES", "linkTitleES");
        element.setField("linkTitlePT", "linkTitlePT");
        element.setField("termsAndConditionsES", "termsAndConditionsES");
        element.setField("termsAndConditionsPT", "termsAndConditionsPT");
        element.setField("descriptionES", "descriptionES");
        element.setField("descriptionPT", "descriptionPT");
        element.setField("companyName", "companyName");
        element.setField("company", 1L);
        element.setField("dateFrom", now);
        element.setField("dateTo", now);
        element.setField("publicationDate", now);
        element.setField("category", "SHP");
        element.setField("isOutstanding", true);
        element.setField("isFree", true);
        element.setField("companyPicture", "foto");
        element.setField("oidBenefit", "1");
        element.setField("name", "value");
        element.setField("relevance", 1);
        result.add(element);
        Group group = new Group("groupValue", result);
        command.add(group);
        SimpleOrderedMap<Object> g = new SimpleOrderedMap<Object>();
        g.add("matches", 0);
        ArrayList<Object> list = new ArrayList<Object>();
        list.add(command);
        g.add("doclist", result);
        NamedList<Object> res = new NamedList<Object>();
        NamedList<Object> a = new NamedList<Object>();
        a.add("groups", g);
        res.add("grouped", a);
        queryResponse.setResponse(res);
        return queryResponse;
    }

}
