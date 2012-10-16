package com.despegar.sobek.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.despegar.framework.spring.response.context.client.ResponseContainer;
import com.despegar.geodespegar.client.GeoAreaClient;
import com.despegar.geodespegar.dto.ConcreteGeoAreaDTO;
import com.despegar.geodespegar.dto.GeoAreaDescriptionDTO;
import com.despegar.geodespegar.dto.GeoAreaOIDsDTO;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;

public class GeoAreaServiceImplTest {

	private GeoAreaServiceImpl instance = new GeoAreaServiceImpl();
	@Mock
	private GeoAreaClient geoAreaClientMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.instance.setGeoAreaClient(geoAreaClientMock);
	}

	@Test
	public void getGeoAreasDescriptions_correct_returnsGeoAreaDescriptions() {
		List<Long> geoAreaOIDs = Lists.newArrayList();

		GeoAreaDescriptionDTO descDTO = new GeoAreaDescriptionDTO();
		descDTO.setLanguageCode("ES");
		descDTO.setName("Buenos Aires");

		List<GeoAreaDescriptionDTO> descriptions = Lists.newArrayList(descDTO);

		ConcreteGeoAreaDTO dto = new ConcreteGeoAreaDTO();
		dto.setDescriptions(descriptions);
		dto.setOID(1L);

		Collection<ConcreteGeoAreaDTO> concreteGeoAreas = Lists.newArrayList(dto);
		ResponseContainer<Collection<ConcreteGeoAreaDTO>> response = new ResponseContainer<Collection<ConcreteGeoAreaDTO>>();
		response.setData(concreteGeoAreas);

		when(geoAreaClientMock.getGeoAreasByOIDList(any(GeoAreaOIDsDTO.class))).thenReturn(response);

		ListMultimap<Long, GeoAreaDescriptionDTO> geoAreasDescriptions = this.instance
				.getGeoAreasDescriptions(geoAreaOIDs);

		verify(geoAreaClientMock).getGeoAreasByOIDList(any(GeoAreaOIDsDTO.class));

		assertNotNull(geoAreasDescriptions);
		assertEquals(1, geoAreasDescriptions.size());
		assertEquals(1, geoAreasDescriptions.get(1L).size());
		assertEquals("Buenos Aires", geoAreasDescriptions.get(1L).get(0).getName());
		assertEquals("ES", geoAreasDescriptions.get(1L).get(0).getLanguageCode());
	}
}
