package com.despegar.sobek.service;

import java.util.List;

import com.despegar.geodespegar.dto.GeoAreaDescriptionDTO;
import com.google.common.collect.ListMultimap;

public interface GeoAreaService {

	public abstract ListMultimap<Long, GeoAreaDescriptionDTO> getGeoAreasDescriptions(List<Long> geoAreaOIDs);

}