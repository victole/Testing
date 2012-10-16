package com.despegar.sobek.translator;

import java.util.ArrayList;
import java.util.Date;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import com.despegar.framework.tests.spring.AbstractTransactionalSpringTest;
import com.despegar.sobek.dto.BenefitFilterResultDTO;
import com.despegar.sobek.dto.BenefitItemResultDTO;

@ContextConfiguration(locations = {"classpath:/com/despegar/test/test-reference-data-context.xml"})
public class BenefitFilterResultTranslatorTest
    extends AbstractTransactionalSpringTest {

    @Resource
    BenefitFilterResultTranslator benefitFilterResultTranslator;

    @Test
    public void translate_nullQueryResponse_benefitFilterResultDTO() {
        QueryResponse queryResponse = null;
        BenefitFilterResultDTO benefitFilterResultDTO = this.benefitFilterResultTranslator.translate(queryResponse);
        Assert.assertEquals(benefitFilterResultDTO.getNumberOfResults().toString(), "0");
        Assert.assertEquals(benefitFilterResultDTO.getBenefits().size(), 0);
    }

    @Test
    public void translate_queryResponse_BenefitFilterResultDTO() {
        Date date = new Date();
        QueryResponse queryResponse = new QueryResponse();
        GroupCommand command = new GroupCommand("name", 0);
        SolrDocumentList result = new SolrDocumentList();
        SolrDocument element = new SolrDocument();
        element.setField("state", "state");
        element.setField("titleES", "titleES");
        element.setField("titlePT", "titlePT");
        element.setField("companyName", "companyName");
        element.setField("dateFrom", date);
        element.setField("dateTo", date);
        element.setField("publicationDate", date);
        element.setField("isOutstanding", true);
        element.setField("oidBenefit", 1);
        element.setField("name", "value");
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
        BenefitFilterResultDTO benefitFilterResultDTO = this.benefitFilterResultTranslator.translate(queryResponse);
        BenefitItemResultDTO benefitItemResultDTO = benefitFilterResultDTO.getBenefits().get(0);
        Assert.assertEquals(benefitItemResultDTO.getBenefitStatusCode(), "state");
        Assert.assertEquals(benefitItemResultDTO.getCompanyName(), "companyName");
        Assert.assertEquals(benefitItemResultDTO.getDateFrom(), date.getTime());
        Assert.assertEquals(benefitItemResultDTO.getDateTo(), date.getTime());
        Assert.assertEquals(benefitItemResultDTO.getOID(), 1);
        Assert.assertEquals(benefitItemResultDTO.getPublicationDate(), date.getTime());
        Assert.assertEquals(benefitItemResultDTO.getIsOutstanding(), true);
        Assert.assertEquals(benefitItemResultDTO.getTitles().get("ES"), "titleES");
        Assert.assertEquals(benefitItemResultDTO.getTitles().get("PT"), "titlePT");
        Assert.assertEquals(benefitFilterResultDTO.getBenefits().size(), 1);

    }

    @Test
    public void translate_solrDocumentEmpty_BenefitFilterResultDTO() {
        new Date();
        QueryResponse queryResponse = new QueryResponse();
        GroupCommand command = new GroupCommand("name", 0);
        SolrDocumentList result = new SolrDocumentList();
        SolrDocument element = new SolrDocument();
        element.setField("state", null);
        element.setField("titleES", null);
        element.setField("titlePT", null);
        element.setField("companyName", null);
        element.setField("dateFrom", null);
        element.setField("dateTo", null);
        element.setField("publicationDate", null);
        element.setField("isOutstanding", null);
        element.setField("oidBenefit", null);
        element.setField("name", null);
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
        BenefitFilterResultDTO benefitFilterResultDTO = this.benefitFilterResultTranslator.translate(queryResponse);
        BenefitItemResultDTO benefitItemResultDTO = benefitFilterResultDTO.getBenefits().get(0);
        Assert.assertEquals(benefitItemResultDTO.getBenefitStatusCode(), null);
        Assert.assertEquals(benefitItemResultDTO.getCompanyName(), null);
        Assert.assertEquals(benefitItemResultDTO.getDateFrom(), 0);
        Assert.assertEquals(benefitItemResultDTO.getDateTo(), 0);
        Assert.assertEquals(benefitItemResultDTO.getOID(), 0);
        Assert.assertEquals(benefitItemResultDTO.getPublicationDate(), 0);
        Assert.assertEquals(benefitItemResultDTO.getIsOutstanding(), null);
        Assert.assertEquals(benefitItemResultDTO.getTitles(), null);
        Assert.assertEquals(benefitItemResultDTO.getTitles(), null);
        Assert.assertEquals(benefitFilterResultDTO.getBenefits().size(), 1);

    }

}
