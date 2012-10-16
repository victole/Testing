package com.despegar.sobek.solr.index.data;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.despegar.framework.spring.response.context.client.ResponseContainer;
import com.despegar.framework.utils.string.StringUtils;
import com.despegar.geodespegar.client.CountryClient;
import com.despegar.geodespegar.dto.CityDTO;
import com.despegar.geodespegar.dto.CountryDTO;
import com.despegar.sobek.dao.GeoAreaDAO;
import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class GeoAreaMemorySnapshot {

    private static final Logger LOGGER = Logger.getLogger(GeoAreaMemorySnapshot.class);

    private static final Integer MAX_CITIES = 1000;
    private static final Integer MAX_COUNTRIES = 25;

    private GeoAreaDAO geoAreaDAO;
    private CountryClient client;

    private volatile Multimap<Long, Long> mapCountriesKeys = ArrayListMultimap.create();
    private volatile Multimap<Long, Long> mapCitiesKeys = ArrayListMultimap.create();


    public void initSnapshot() {
        this.mapCitiesKeys = this.generateMapCitiesKeys();
        this.mapCountriesKeys = this.generateMapCountiesKeys();
    }

    public void deleteSnapshot() {
        this.mapCountriesKeys = ArrayListMultimap.create();
        this.mapCitiesKeys = ArrayListMultimap.create();
        LOGGER.info("Se borran los Snapshot de geo");
    }

    public Long getCountry(Long cityOID) {
        Long returnOID;

        Collection<Long> countriesOid = this.mapCitiesKeys.get(cityOID);

        if (!countriesOid.isEmpty()) {
            returnOID = countriesOid.iterator().next();
        } else {
            // al servicio
            Long[] citiesOIDs = {cityOID};
            Map<Long, CountryDTO> countriesByCityListOID = this.getCountriesByCityListOid(citiesOIDs);
            Preconditions.checkState(countriesByCityListOID != null,
                "getCountriesByCityListOid() with oid = %s returns null", cityOID);
            returnOID = countriesByCityListOID.get(cityOID).getOID();
        }

        return returnOID;
    }

    public List<Long> getCities(Long countryOID) {
        List<Long> returnOIDs = new LinkedList<Long>();

        Multimap<Long, Long> mapCountriesKeysTemp = this.mapCountriesKeys;

        Collection<Long> citiesOid = mapCountriesKeysTemp.get(countryOID);

        if (!citiesOid.isEmpty()) {
            returnOIDs = (List<Long>) citiesOid;
        } else {
            // al servicio
            Long[] OIDs = {countryOID};
            Map<Long, Collection<CityDTO>> citiesByCountriesListOID = this.citiesByCountriesListOID(OIDs);
            for (Entry<Long, Collection<CityDTO>> data : citiesByCountriesListOID.entrySet()) {
                for (CityDTO cityDTO : data.getValue()) {
                    returnOIDs.add(cityDTO.getOID());
                }
            }
        }
        return returnOIDs;
    }



    private Multimap<Long, Long> generateMapCountiesKeys() {
        LOGGER.info("Se genera Snapshot las countries");
        List<Long> countriesOIDs = this.geoAreaDAO.getGeoAreaOIDs("P");
        Multimap<Long, Long> mapCountriesKeysTemp = ArrayListMultimap.create();
        List<Long> OIDs = null;
        Integer fromIndex = 0;
        Integer toIndex = MAX_COUNTRIES;
        while (fromIndex < countriesOIDs.size()) {
            if (toIndex > countriesOIDs.size()) {
                toIndex = countriesOIDs.size();
            }
            OIDs = countriesOIDs.subList(fromIndex, toIndex);
            mapCountriesKeysTemp.putAll(this.getCountriesMap(OIDs));
            fromIndex += MAX_COUNTRIES;
            toIndex = fromIndex + MAX_COUNTRIES;
        }
        LOGGER.info("Finaliza Snapshot de las countries");
        return mapCountriesKeysTemp;

    }

    private Multimap<Long, Long> generateMapCitiesKeys() {
        LOGGER.info("Se genera Snapshot las cities");
        List<Long> citiesOIDs = this.geoAreaDAO.getGeoAreaOIDs("C");
        Multimap<Long, Long> mapCitiesKeysTemp = ArrayListMultimap.create();
        List<Long> OIDs = null;
        Integer fromIndex = 0;
        Integer toIndex = MAX_CITIES;
        while (fromIndex < citiesOIDs.size()) {
            if (toIndex > citiesOIDs.size()) {
                toIndex = citiesOIDs.size();
            }
            OIDs = citiesOIDs.subList(fromIndex, toIndex);
            mapCitiesKeysTemp.putAll(this.getCitiesMap(OIDs));
            fromIndex += MAX_CITIES;
            toIndex = fromIndex + MAX_CITIES;
        }
        LOGGER.info("Finaliza Snapshot de las cities");
        return mapCitiesKeysTemp;
    }

    private Multimap<Long, Long> getCitiesMap(List<Long> citiesOIDs) {
        Multimap<Long, Long> mapCitiesKeysTemp = ArrayListMultimap.create();
        Long[] OIDs = citiesOIDs.toArray(new Long[0]);

        Map<Long, CountryDTO> countriesByCityListOID = this.getCountriesByCityListOid(OIDs);

        Preconditions
            .checkState(countriesByCityListOID != null, "getCountriesByCityListOid() with oids = [%s] returns null",
                org.apache.commons.lang.StringUtils.join(OIDs, "-"));

        for (Entry<Long, CountryDTO> data : countriesByCityListOID.entrySet()) {
            mapCitiesKeysTemp.put(data.getKey(), data.getValue().getOID());
        }
        return mapCitiesKeysTemp;
    }

    private Multimap<Long, Long> getCountriesMap(List<Long> countriesOIDs) {

        Multimap<Long, Long> mapCountriesKeysTemp = ArrayListMultimap.create();

        Long[] OIDs = countriesOIDs.toArray(new Long[0]);

        Map<Long, Collection<CityDTO>> citiesByCountriesListOID = this.citiesByCountriesListOID(OIDs);

        Preconditions.checkState(citiesByCountriesListOID != null,
            "citiesByCountriesListOID() with oids = [%s] returns null", org.apache.commons.lang.StringUtils.join(OIDs, "-"));

        for (Entry<Long, Collection<CityDTO>> data : citiesByCountriesListOID.entrySet()) {
            for (CityDTO cityDTO : data.getValue()) {
                mapCountriesKeysTemp.put(data.getKey(), cityDTO.getOID());
            }
        }

        return mapCountriesKeysTemp;
    }

    private Map<Long, CountryDTO> getCountriesByCityListOid(Long[] OIDs) {
        LOGGER.info(StringUtils.concat("Se van a buscar a geo getCountriesByCityListOid",
            org.apache.commons.lang.StringUtils.join(OIDs, "-")));

        ResponseContainer<Map<Long, CountryDTO>> countriesByCityListOID = this.client.getCountriesByCityListOid(OIDs);
        return countriesByCityListOID.getData();
    }

    private Map<Long, Collection<CityDTO>> citiesByCountriesListOID(Long[] OIDs) {
        LOGGER.info(StringUtils.concat("Se van a buscar a geo getCitiesByCountryListOid",
            org.apache.commons.lang.StringUtils.join(OIDs, "-")));

        ResponseContainer<Map<Long, Collection<CityDTO>>> citiesByCountriesListOID = this.client
            .getCitiesByCountryListOid(OIDs);
        return citiesByCountriesListOID.getData();
    }


    public void setClient(CountryClient client) {
        this.client = client;
    }

    public void setGeoAreaDAO(GeoAreaDAO geoAreaDAO) {
        this.geoAreaDAO = geoAreaDAO;
    }



}
