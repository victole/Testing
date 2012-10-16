package com.despegar.sobek.solr.index.manager;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.Times;

import com.despegar.sobek.dao.BenefitIndexDAO;
import com.despegar.sobek.dto.BenefitFilterContainerDTO;
import com.despegar.sobek.dto.BenefitFilterDTO;
import com.despegar.sobek.dto.OrderDTO;
import com.despegar.sobek.dto.OrderDTO.OrderByType;
import com.despegar.sobek.dto.OrderDTO.OrderDirectionType;
import com.despegar.sobek.model.Benefit;
import com.despegar.sobek.solr.index.data.GeoAreaMemorySnapshot;
import com.despegar.sobek.solr.model.BenefitIndex;
import com.despegar.sobek.translator.BenefitIndexSolrTranslator;
import com.google.common.collect.Lists;

public class BenefitIndexManagerTest {

    private BenefitIndexManager instance = new BenefitIndexManager();

    @Mock
    private BenefitIndexSolrTranslator benefitIndexSolrTranslatorMock;

    @Mock
    private BenefitIndexDAO benefitIndexDAOMock;

    @Mock
    private GeoAreaMemorySnapshot geoAreaMemorySnapshotMock;

    @Mock
    private SolrServer solrConnectorMock;

    @Mock
    private Map<String, String> mapSortValuesMock;


    @Before
    public void setUp() {


        MockitoAnnotations.initMocks(this);

        this.instance.setBenefitIndexDAO(this.benefitIndexDAOMock);
        this.instance.setBenefitIndexSolrTranslator(this.benefitIndexSolrTranslatorMock);
        this.instance.setGeoAreaMemorySnapshot(this.geoAreaMemorySnapshotMock);
        this.instance.setSolrConnector(this.solrConnectorMock);
        this.instance.setMapSortValues(this.mapSortValuesMock);
        List<Benefit> benefits = new LinkedList<Benefit>();
        List<BenefitIndex> list = new LinkedList<BenefitIndex>();

        when(this.mapSortValuesMock.get(any(String.class))).thenReturn("");
        when(this.benefitIndexDAOMock.getBenefitIndexes(null)).thenReturn(benefits);
        when(this.benefitIndexSolrTranslatorMock.translatorList(benefits)).thenReturn(list);
        Mockito.doNothing().when(this.geoAreaMemorySnapshotMock).initSnapshot();

        UpdateResponse response = new UpdateResponse();
        try {
            when(this.solrConnectorMock.commit()).thenReturn(response);
            QueryResponse queryResponse = new QueryResponse();
            when(this.solrConnectorMock.query(any(SolrQuery.class))).thenReturn(queryResponse);
        } catch (Exception e) {
            Assert.fail();
        }
    }


    @Test
    public void index_indexing_OK() {
        this.instance.index();

        List<Benefit> benefits = new LinkedList<Benefit>();
        verify(this.benefitIndexDAOMock).getBenefitIndexes(null);
        verify(this.geoAreaMemorySnapshotMock).initSnapshot();
        verify(this.benefitIndexSolrTranslatorMock).translatorList(benefits);
        try {
            verify(this.solrConnectorMock, new Times(2)).commit();
        } catch (Exception e) {
            Assert.fail();
        }

    }

    @Test
    public void update_updateElement_OK() {
        List<Long> benefitOIDs = new LinkedList<Long>();
        benefitOIDs.add(2L);
        List<Benefit> benefits = new LinkedList<Benefit>();
        when(this.benefitIndexDAOMock.getBenefitIndexes(benefitOIDs)).thenReturn(benefits);
        this.instance.update(benefitOIDs);
        verify(this.benefitIndexDAOMock).getBenefitIndexes(benefitOIDs);
        verify(this.benefitIndexSolrTranslatorMock).translatorList(benefits);
        try {
            verify(this.solrConnectorMock).commit();
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void delete_appliancesOIDsNULL_save() {
        List<Long> appliancesOIDs = new LinkedList<Long>();
        this.instance.delete(appliancesOIDs);
        try {
            verify(this.solrConnectorMock, new Times(0)).commit();
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void delete_appliancesOIDs_save() {
        List<Long> appliancesOIDs = new LinkedList<Long>();
        appliancesOIDs.add(2L);
        this.instance.delete(appliancesOIDs);
        try {
            verify(this.solrConnectorMock).commit();
        } catch (Exception e) {
            Assert.fail();
        }
    }


    @Test
    public void query_benefitFilterContainerDTOWithOrdersNull_OK() {
        BenefitFilterContainerDTO benefitFilterContainerDTO = this.createBenefitFilterContainerDTO();
        benefitFilterContainerDTO.setOrders(null);
        this.instance.search(benefitFilterContainerDTO);
        try {
            verify(this.solrConnectorMock).query(any(SolrQuery.class));
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void query_benefitFilterContainerDTOWithOrderDESC_OK() {
        BenefitFilterContainerDTO benefitFilterContainerDTO = this.createBenefitFilterContainerDTO();

        List<OrderDTO> orders = Lists.newArrayList();
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderBy(OrderByType.IS_OUTSTANDING);
        orderDTO.setOrderDirection(OrderDirectionType.DESC);
        orders.add(orderDTO);

        OrderDTO orderDTO2 = new OrderDTO();
        orderDTO2.setOrderBy(OrderByType.PUBLICATION_DATE);
        orderDTO2.setOrderDirection(OrderDirectionType.DESC);
        orders.add(orderDTO2);
        benefitFilterContainerDTO.setOrders(orders);
        this.instance.search(benefitFilterContainerDTO);
        try {
            verify(this.solrConnectorMock).query(any(SolrQuery.class));
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void query_benefitFilterContainerDTOWithOrderASC_OK() {
        BenefitFilterContainerDTO benefitFilterContainerDTO = this.createBenefitFilterContainerDTO();
        this.instance.search(benefitFilterContainerDTO);
        try {
            verify(this.solrConnectorMock).query(any(SolrQuery.class));
        } catch (Exception e) {
            Assert.fail();
        }
    }



    @Test
    public void generateQuery_benefitFilterContainerDTO_OK() {
        Set<BenefitFilterDTO> benefitFilterDTOs = this.createBenefitFilterDTO();
        String query = this.instance.generateQuery(benefitFilterDTOs);
        Assert
            .assertEquals(
                query,
                "((((category:GST) AND (company:1) AND (citiesTo:0 OR citiesTo:982) AND (citiesFrom:0 OR citiesFrom:5693)) AND (((dateFrom:[* TO 2011-10-21T03:00:00Z]) NOT (dateTo:[* TO 2011-10-21T03:00:00Z-1MINUTE])) OR ((dateFrom:[* TO 2011-10-21T03:00:00Z]) NOT (dateTo:[* TO 2011-10-21T03:00:00Z-1MINUTE])) OR ((dateFrom:[2011-10-21T03:00:00Z TO 2011-10-21T03:00:00Z-1MINUTE]) AND (dateTo:[2011-10-21T03:00:00Z TO 2011-10-21T03:00:00Z-1MINUTE]))) AND (product:H OR product:F OR product:P OR product:ALL) AND (state:PUB OR state:CAN OR state:UNP) AND (brand:ALL OR brand:DESAR)))");
    }

    @Test
    public void generateQuery_benefitFilterContainerDTOWithDestination_OK() {
        Set<BenefitFilterDTO> benefitFilterDTOs = this.createBenefitFilterDTO();
        benefitFilterDTOs.iterator().next().setDestinationOID(982L);
        benefitFilterDTOs.iterator().next().setDestinationType("P");
        benefitFilterDTOs.iterator().next().setOriginOID(5693L);
        benefitFilterDTOs.iterator().next().setOriginType("P");

        String query = this.instance.generateQuery(benefitFilterDTOs);
        Assert
            .assertEquals(
                query,
                "((((category:GST) AND (company:1) AND (countryTo:0 OR countryTo:982) AND (countryFrom:0 OR countryFrom:5693)) AND (((dateFrom:[* TO 2011-10-21T03:00:00Z]) NOT (dateTo:[* TO 2011-10-21T03:00:00Z-1MINUTE])) OR ((dateFrom:[* TO 2011-10-21T03:00:00Z]) NOT (dateTo:[* TO 2011-10-21T03:00:00Z-1MINUTE])) OR ((dateFrom:[2011-10-21T03:00:00Z TO 2011-10-21T03:00:00Z-1MINUTE]) AND (dateTo:[2011-10-21T03:00:00Z TO 2011-10-21T03:00:00Z-1MINUTE]))) AND (product:H OR product:F OR product:P OR product:ALL) AND (state:PUB OR state:CAN OR state:UNP) AND (brand:ALL OR brand:DESAR)))");
    }

    @Test
    public void generateQuery_benefitFilterContainerDTOWithDateFrom_OK() {
        Set<BenefitFilterDTO> benefitFilterDTOs = this.createBenefitFilterDTO();
        benefitFilterDTOs.iterator().next().setDateFrom(null);
        String query = this.instance.generateQuery(benefitFilterDTOs);
        Assert
            .assertEquals(
                query,
                "((((category:GST) AND (company:1) AND (citiesTo:0 OR citiesTo:982) AND (citiesFrom:0 OR citiesFrom:5693)) AND ((dateFrom:[* TO 2011-10-21T03:00:00Z]) NOT (dateTo:[* TO 2011-10-21T03:00:00Z-1MINUTE])) AND (product:H OR product:F OR product:P OR product:ALL) AND (state:PUB OR state:CAN OR state:UNP) AND (brand:ALL OR brand:DESAR)))");
    }

    @Test
    public void generateQuery_benefitFilterContainerDTOWithDateTo_OK() {
        Set<BenefitFilterDTO> benefitFilterDTOs = this.createBenefitFilterDTO();
        benefitFilterDTOs.iterator().next().setDateTo(null);
        String query = this.instance.generateQuery(benefitFilterDTOs);
        Assert
            .assertEquals(
                query,
                "((((category:GST) AND (company:1) AND (citiesTo:0 OR citiesTo:982) AND (citiesFrom:0 OR citiesFrom:5693)) AND ((dateFrom:[* TO 2011-10-21T03:00:00Z]) NOT (dateTo:[* TO 2011-10-21T03:00:00Z-1MINUTE])) AND (product:H OR product:F OR product:P OR product:ALL) AND (state:PUB OR state:CAN OR state:UNP) AND (brand:ALL OR brand:DESAR)))");
    }

    @Test
    public void generateQuery_benefitFilterContainerDTOWithDateTos_OK() {
        Set<BenefitFilterDTO> benefitFilterDTOs = this.createBenefitFilterDTO();
        benefitFilterDTOs.iterator().next().setDateFrom(1319166000000L);
        String query = this.instance.generateQuery(benefitFilterDTOs);
        Assert
            .assertEquals(
                query,
                "((((category:GST) AND (company:1) AND (citiesTo:0 OR citiesTo:982) AND (citiesFrom:0 OR citiesFrom:5693)) AND (((dateFrom:[* TO 2011-10-21T03:00:00Z]) NOT (dateTo:[* TO 2011-10-21T03:00:00Z-1MINUTE])) OR ((dateFrom:[* TO 2011-10-21T03:00:00Z]) NOT (dateTo:[* TO 2011-10-21T03:00:00Z-1MINUTE])) OR ((dateFrom:[2011-10-21T03:00:00Z TO 2011-10-21T03:00:00Z-1MINUTE]) AND (dateTo:[2011-10-21T03:00:00Z TO 2011-10-21T03:00:00Z-1MINUTE]))) AND (product:H OR product:F OR product:P OR product:ALL) AND (state:PUB OR state:CAN OR state:UNP) AND (brand:ALL OR brand:DESAR)))");


    }

    private Set<BenefitFilterDTO> createBenefitFilterDTO() {

        BenefitFilterDTO benefitFilterDTO = new BenefitFilterDTO();
        List<String> appliesTo = new LinkedList<String>();
        appliesTo.add("H");
        appliesTo.add("F");
        appliesTo.add("P");
        benefitFilterDTO.setAppliesTo(appliesTo);
        List<String> benefitStatusCodes = new LinkedList<String>();
        benefitStatusCodes.add("PUB");
        benefitStatusCodes.add("CAN");
        benefitStatusCodes.add("UNP");
        benefitFilterDTO.setBenefitStatusCodes(benefitStatusCodes);
        benefitFilterDTO.setBrandCode("DESAR");
        benefitFilterDTO.setCategoryCode("GST");
        benefitFilterDTO.setCompanyOID(1L);
        benefitFilterDTO.setDateFrom(1319166000000L);
        benefitFilterDTO.setDateTo(1319166000000L);
        benefitFilterDTO.setDestinationOID(982L);
        benefitFilterDTO.setDestinationType("C");
        benefitFilterDTO.setOriginOID(5693L);
        benefitFilterDTO.setOriginType("C");

        Set<BenefitFilterDTO> benefitFilterDTOs = new HashSet<BenefitFilterDTO>();
        benefitFilterDTOs.add(benefitFilterDTO);

        return benefitFilterDTOs;
    }

    private BenefitFilterContainerDTO createBenefitFilterContainerDTO() {
        BenefitFilterContainerDTO benefitFilterContainerDTO = new BenefitFilterContainerDTO();
        benefitFilterContainerDTO.setBenefitFilterDTOs(this.createBenefitFilterDTO());

        benefitFilterContainerDTO.setPageNumber(1);
        benefitFilterContainerDTO.setPageSize(10);

        List<OrderDTO> orders = Lists.newArrayList();
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderBy(orderDTO.getOrderBy().IS_OUTSTANDING);
        orderDTO.setOrderDirection(orderDTO.getOrderDirection().ASC);
        orders.add(orderDTO);

        OrderDTO orderDTO2 = new OrderDTO();
        orderDTO2.setOrderBy(orderDTO.getOrderBy().PUBLICATION_DATE);
        orderDTO2.setOrderDirection(orderDTO.getOrderDirection().ASC);
        orders.add(orderDTO2);

        benefitFilterContainerDTO.setOrders(orders);
        return benefitFilterContainerDTO;
    }

}
