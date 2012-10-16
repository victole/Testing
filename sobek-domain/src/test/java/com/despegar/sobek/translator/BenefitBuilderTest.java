package com.despegar.sobek.translator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.despegar.sobek.dto.BenefitContainerDTO;
import com.despegar.sobek.dto.BenefitDTO;
import com.despegar.sobek.dto.BenefitFilterContainerDTO;
import com.despegar.sobek.dto.BenefitFilterDTO;
import com.despegar.sobek.dto.ClientDTO;
import com.despegar.sobek.model.GeoArea;
import com.despegar.sobek.strategy.DefaultURLGenerationStrategy;
import com.despegar.sobek.strategy.HashcodeURLGenerationStrategy;
import com.despegar.sobek.strategy.URLGenerationStrategy;
import com.google.common.collect.Lists;

public class BenefitBuilderTest {

    private BenefitBuilder instance = new BenefitBuilder();
    @Mock
    private HashcodeURLGenerationStrategy hashcodeURLGenerationStrategyMock;
    @Mock
    private DefaultURLGenerationStrategy defaultURLGenerationStrategyMock;
    @Mock
    private SolrBenefitTranslator solrBenefitTranslatorMock;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.instance.setSolrBenefitTranslator(this.solrBenefitTranslatorMock);
        Map<String, URLGenerationStrategy> strategies = new HashMap<String, URLGenerationStrategy>();
        strategies.put("HASHCODE_RENDERING", this.hashcodeURLGenerationStrategyMock);
        strategies.put("DEFAULT", this.defaultURLGenerationStrategyMock);
        this.instance.setUrlGenerationStrategies(strategies);
        Map<String, String> brandLanguageMap = new HashMap<String, String>();
        brandLanguageMap.put("DESBR", "PT");
        brandLanguageMap.put("DEFAULT", "ES");
        this.instance.setBrandLanguageMap(brandLanguageMap);
    }

    @Test
    public void build_correctParametersHashcodeStrategy_returnsBenefitContainerDTO() {
        Date now = new Date();
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.setTime(now);
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);

        QueryResponse queryResponse = new QueryResponse();
        GeoArea geoArea = new GeoArea();
        geoArea.setDespegarItemOID(1L);
        geoArea.setOID(1L);
        BenefitFilterContainerDTO benefitFilterContainerDTO = new BenefitFilterContainerDTO();
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setName("Juan");
        clientDTO.setLastName("Perez");
        clientDTO.setEmail("juan.perez@gmail.com");
        benefitFilterContainerDTO.setClientDTO(clientDTO);
        benefitFilterContainerDTO.setPageNumber(1);
        benefitFilterContainerDTO.setPageSize(10);
        Set<BenefitFilterDTO> benefitFilterDTOs = new HashSet<BenefitFilterDTO>();
        BenefitFilterDTO benefitFilterDTO = new BenefitFilterDTO();
        benefitFilterDTO.setDateFrom(now.getTime());
        benefitFilterDTO.setDateTo(tomorrow.getTime().getTime());
        benefitFilterDTO.setDestinationOID(982L);
        benefitFilterDTO.setDestinationType("C");
        benefitFilterDTOs.add(benefitFilterDTO);
        benefitFilterContainerDTO.setBenefitFilterDTOs(benefitFilterDTOs);

        BenefitContainerDTO benefitContainerDTO = new BenefitContainerDTO();
        benefitContainerDTO.setNumberOfResults(1);
        BenefitDTO benefitDTO = new BenefitDTO();
        benefitDTO.setBenefitStatusCode("PUB");
        benefitDTO.setDateFrom(now.getTime());

        benefitDTO.setDateTo(tomorrow.getTime().getTime());
        benefitDTO.setIsFree(Boolean.FALSE);
        benefitDTO.setLinkTemplateType("HASHCODE_RENDERING");
        benefitContainerDTO.setBenefitDTOs(new ArrayList<BenefitDTO>());
        benefitContainerDTO.getBenefitDTOs().add(benefitDTO);

        Set<Long> destinationIds = new HashSet<Long>();
        destinationIds.add(982L);

        when(this.solrBenefitTranslatorMock.buildBenefitList(queryResponse)).thenReturn(benefitContainerDTO);
        when(
            this.hashcodeURLGenerationStrategyMock.generateURL(benefitDTO, destinationIds, "ES", "Juan Perez", now,
                tomorrow.getTime(), null)).thenReturn("http://www.estaeslaurl.com");

        BenefitContainerDTO retrievedBenefitContainer = this.instance.build(queryResponse, benefitFilterContainerDTO);

        assertEquals(1, retrievedBenefitContainer.getNumberOfResults().intValue());
        assertEquals(1, retrievedBenefitContainer.getBenefitDTOs().size());
        assertEquals(now.getTime(), retrievedBenefitContainer.getBenefitDTOs().get(0).getDateFrom().longValue());
        assertEquals("http://www.estaeslaurl.com", retrievedBenefitContainer.getBenefitDTOs().get(0).getExternalResources()
            .get("ES"));
    }

    @Test
    public void build_correctParametersDefaultStrategy_returnsBenefitContainerDTO() {
        Date now = new Date();
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.setTime(now);
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);

        QueryResponse queryResponse = new QueryResponse();
        GeoArea geoArea = new GeoArea();
        geoArea.setDespegarItemOID(1L);
        geoArea.setOID(1L);
        BenefitFilterContainerDTO benefitFilterContainerDTO = new BenefitFilterContainerDTO();
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setName("Juan");
        clientDTO.setLastName("Perez");
        clientDTO.setEmail("juan.perez@gmail.com");
        benefitFilterContainerDTO.setClientDTO(clientDTO);
        benefitFilterContainerDTO.setPageNumber(1);
        benefitFilterContainerDTO.setPageSize(10);
        Set<BenefitFilterDTO> benefitFilterDTOs = new HashSet<BenefitFilterDTO>();
        BenefitFilterDTO benefitFilterDTO = new BenefitFilterDTO();
        benefitFilterDTO.setDateFrom(now.getTime());
        benefitFilterDTO.setDateTo(tomorrow.getTime().getTime());
        benefitFilterDTO.setDestinationOID(982L);
        benefitFilterDTO.setDestinationType("C");
        benefitFilterDTOs.add(benefitFilterDTO);
        benefitFilterContainerDTO.setBenefitFilterDTOs(benefitFilterDTOs);

        BenefitContainerDTO benefitContainerDTO = new BenefitContainerDTO();
        benefitContainerDTO.setNumberOfResults(1);
        BenefitDTO benefitDTO = new BenefitDTO();
        benefitDTO.setBenefitStatusCode("PUB");
        benefitDTO.setDateFrom(now.getTime());

        benefitDTO.setDateTo(tomorrow.getTime().getTime());
        benefitDTO.setIsFree(Boolean.FALSE);
        benefitDTO.setLinkTemplateType("CONTEXT_RENDERING");
        benefitContainerDTO.setBenefitDTOs(new ArrayList<BenefitDTO>());
        benefitContainerDTO.getBenefitDTOs().add(benefitDTO);

        Set<Long> destinationIds = new HashSet<Long>();
        destinationIds.add(982L);

        when(this.solrBenefitTranslatorMock.buildBenefitList(queryResponse)).thenReturn(benefitContainerDTO);
        when(
            this.defaultURLGenerationStrategyMock.generateURL(benefitDTO, destinationIds, "ES", "Juan Perez", now,
                tomorrow.getTime(), "")).thenReturn(null);

        BenefitContainerDTO retrievedBenefitContainer = this.instance.build(queryResponse, benefitFilterContainerDTO);

        assertEquals(1, retrievedBenefitContainer.getNumberOfResults().intValue());
        assertEquals(1, retrievedBenefitContainer.getBenefitDTOs().size());
        assertEquals(now.getTime(), retrievedBenefitContainer.getBenefitDTOs().get(0).getDateFrom().longValue());
        assertNull(retrievedBenefitContainer.getBenefitDTOs().get(0).getExternalResources().get("ES"));
    }

    @Test(expected = NullPointerException.class)
    public void build_noDates_throwException() {
        Date now = new Date();
        QueryResponse queryResponse = new QueryResponse();
        GeoArea geoArea = new GeoArea();
        geoArea.setDespegarItemOID(1L);
        geoArea.setOID(1L);
        BenefitFilterContainerDTO benefitFilterContainerDTO = new BenefitFilterContainerDTO();
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setName("Juan");
        clientDTO.setLastName("Perez");
        clientDTO.setEmail("juan.perez@gmail.com");
        benefitFilterContainerDTO.setClientDTO(clientDTO);
        benefitFilterContainerDTO.setPageNumber(1);
        benefitFilterContainerDTO.setPageSize(10);
        Set<BenefitFilterDTO> benefitFilterDTOs = new HashSet<BenefitFilterDTO>();
        BenefitFilterDTO benefitFilterDTO = new BenefitFilterDTO();
        benefitFilterDTO.setDestinationOID(982L);
        benefitFilterDTO.setDestinationType("C");
        benefitFilterDTOs.add(benefitFilterDTO);
        benefitFilterContainerDTO.setBenefitFilterDTOs(benefitFilterDTOs);

        BenefitContainerDTO benefitContainerDTO = new BenefitContainerDTO();
        benefitContainerDTO.setNumberOfResults(1);
        BenefitDTO benefitDTO = new BenefitDTO();
        benefitDTO.setBenefitStatusCode("PUB");
        benefitDTO.setIsFree(Boolean.FALSE);
        benefitDTO.setLinkTemplateType("HASHCODE_RENDERING");
        benefitContainerDTO.setBenefitDTOs(new ArrayList<BenefitDTO>());
        benefitContainerDTO.getBenefitDTOs().add(benefitDTO);

        Set<Long> destinationIds = new HashSet<Long>();
        destinationIds.add(1L);

        when(this.solrBenefitTranslatorMock.buildBenefitList(any(QueryResponse.class))).thenReturn(benefitContainerDTO);
        when(
            this.hashcodeURLGenerationStrategyMock.generateURL(benefitDTO, destinationIds, "ES", "Juan Perez", now, now, ""))
            .thenReturn("http://www.estaeslaurl.com");

        this.instance.build(queryResponse, benefitFilterContainerDTO);
    }

    @Test
    public void build_noParameters_returnsUrl() {
        new Date();
        QueryResponse queryResponse = new QueryResponse();
        BenefitFilterContainerDTO benefitFilterContainerDTO = new BenefitFilterContainerDTO();
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setEmail("juan.perez@gmail.com");
        benefitFilterContainerDTO.setClientDTO(clientDTO);
        benefitFilterContainerDTO.setPageNumber(1);
        benefitFilterContainerDTO.setPageSize(10);
        Set<BenefitFilterDTO> benefitFilterDTOs = new HashSet<BenefitFilterDTO>();
        benefitFilterContainerDTO.setBenefitFilterDTOs(benefitFilterDTOs);

        BenefitContainerDTO benefitContainerDTO = new BenefitContainerDTO();
        benefitContainerDTO.setNumberOfResults(0);
        benefitContainerDTO.setBenefitDTOs(new ArrayList<BenefitDTO>());
        Lists.newArrayList();

        when(this.solrBenefitTranslatorMock.buildBenefitList(any(QueryResponse.class))).thenReturn(benefitContainerDTO);
        when(this.hashcodeURLGenerationStrategyMock.generateURL(null, null, null, null, null, null, null)).thenReturn(
            "http://www.estaeslaurl.com");

        this.instance.build(queryResponse, benefitFilterContainerDTO);
    }

}
