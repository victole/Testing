package com.despegar.sobek.solr.index.manager;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import com.despegar.framework.tests.spring.AbstractTransactionalSpringTest;
import com.despegar.sobek.dao.BenefitDAO;

@ContextConfiguration(locations = {"classpath:/com/despegar/test/test-reference-data-context.xml"})
@Ignore
public class BenefitIndexManagerIntegrationTest
    extends AbstractTransactionalSpringTest {

    @Resource
    BenefitIndexManager benefitIndexManager;
    @Resource
    BenefitDAO benefitDAO;

    @Test
    public void update() {
        List<Long> benefitOIDs = new LinkedList<Long>();
        benefitOIDs.add(3906L);
        this.benefitIndexManager.update(benefitOIDs);
    }


    @Test
    public void index() {
        this.benefitIndexManager.index();
    }

    @Test
    public void delete() {
        List<Long> appliancesOIDs = new LinkedList<Long>();
        appliancesOIDs.add(4182L);
        this.benefitIndexManager.delete(appliancesOIDs);
    }

}
