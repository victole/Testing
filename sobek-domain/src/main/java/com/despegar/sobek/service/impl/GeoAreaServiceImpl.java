package com.despegar.sobek.service.impl;

import java.util.Collection;
import java.util.List;

import com.despegar.geodespegar.client.GeoAreaClient;
import com.despegar.geodespegar.dto.ConcreteGeoAreaDTO;
import com.despegar.geodespegar.dto.GeoAreaDescriptionDTO;
import com.despegar.geodespegar.dto.GeoAreaOIDsDTO;
import com.despegar.sobek.service.GeoAreaService;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

public class GeoAreaServiceImpl implements GeoAreaService {

	private GeoAreaClient geoAreaClient;

	@Override
	public ListMultimap<Long, GeoAreaDescriptionDTO> getGeoAreasDescriptions(List<Long> geoAreaOIDs) {

		ListMultimap<Long, GeoAreaDescriptionDTO> geoAreasDescriptions = ArrayListMultimap.create();

		GeoAreaOIDsDTO geoAreaOIDsDTO = new GeoAreaOIDsDTO();
		geoAreaOIDsDTO.setGeoAreaOIDs(geoAreaOIDs);
		Collection<ConcreteGeoAreaDTO> geoAreas = this.geoAreaClient.getGeoAreasByOIDList(geoAreaOIDsDTO).getData();

		for (ConcreteGeoAreaDTO geoAreaDTO : geoAreas) {
			geoAreasDescriptions.putAll(geoAreaDTO.getOID(), geoAreaDTO.getDescriptions());
		}

		return geoAreasDescriptions;
	}

	public void setGeoAreaClient(GeoAreaClient geoAreaClient) {
		this.geoAreaClient = geoAreaClient;
	}
}
