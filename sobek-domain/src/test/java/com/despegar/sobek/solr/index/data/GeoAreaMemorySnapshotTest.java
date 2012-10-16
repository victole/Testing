package com.despegar.sobek.solr.index.data;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.Times;
import org.springframework.test.context.ContextConfiguration;

import com.despegar.framework.spring.response.context.client.ResponseContainer;
import com.despegar.framework.tests.spring.AbstractTransactionalSpringTest;
import com.despegar.geodespegar.client.CountryClient;
import com.despegar.geodespegar.dto.CityDTO;
import com.despegar.geodespegar.dto.CountryDTO;
import com.despegar.sobek.dao.GeoAreaDAO;

@ContextConfiguration(locations = {"classpath:com/despegar/test/test-reference-data-context.xml"})
public class GeoAreaMemorySnapshotTest
    extends AbstractTransactionalSpringTest {

    @Mock
    private GeoAreaDAO geoAreaDAOMock;

    @Mock
    private CountryClient clientMock;

    @Resource
    GeoAreaMemorySnapshot instance;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.instance = new GeoAreaMemorySnapshot();
        this.instance.setClient(this.clientMock);
        this.instance.setGeoAreaDAO(this.geoAreaDAOMock);
        Long[] citiesOIDs = this.createGeoAreaDAO().toArray(new Long[0]);
        when(this.geoAreaDAOMock.getGeoAreaOIDs("C")).thenReturn(this.createGeoAreaDAO());
        when(this.clientMock.getCountriesByCityListOid(citiesOIDs)).thenReturn(this.createResponseContainerCountry(15));

        Long[] countriesOIDs = this.createGeoAreaDAO().toArray(new Long[0]);
        when(this.geoAreaDAOMock.getGeoAreaOIDs("P")).thenReturn(this.createGeoAreaDAO());
        when(this.clientMock.getCitiesByCountryListOid(countriesOIDs)).thenReturn(this.createResponseContainerCity(15));
    }

    @Test
    public void getCities_getCitiesByCountryListOidInitialization_OK() {

        Long[] countriesOIDs = {1L};
        when(this.clientMock.getCitiesByCountryListOid(countriesOIDs)).thenReturn(this.createResponseContainerCity(1));
        List<Long> citiesOIDs = this.instance.getCities(1L);

        for (Integer i = 1; i < 15; i++) {
            Assert.assertEquals(i.toString(), citiesOIDs.get(i).toString());
        }
    }


    @Test
    public void getCountry_getCountriesByCityListOidInitialization_OK() {
        Long[] citiesOIDs = {1L};
        when(this.clientMock.getCountriesByCityListOid(citiesOIDs)).thenReturn(this.createResponseContainerCountry(15));
        Long oid = this.instance.getCountry(1L);
        Assert.assertEquals(oid.toString(), "1");
    }

    @Test
    public void initSnapshot_CreateMap_OK() {
        Long[] countriesOIDs = {1L};
        when(this.clientMock.getCitiesByCountryListOid(countriesOIDs)).thenReturn(this.createResponseContainerCity(1));
        Long[] citiesOIDs = {1L};
        when(this.clientMock.getCountriesByCityListOid(citiesOIDs)).thenReturn(this.createResponseContainerCountry(1));
        this.instance.initSnapshot();
        Long[] list = new Long[15];
        for (Integer i = 0; i < 15; i++) {
            list[i] = Long.parseLong(i.toString());
        }
        verify(this.clientMock, new Times(1)).getCitiesByCountryListOid(list);
        verify(this.clientMock, new Times(1)).getCountriesByCityListOid(list);
    }

    @Test
    public void deleteSnapshot_deleteSnapshot_OK() {
        /*
         * Long[] countriesOIDs = {1L};
         * when(this.clientMock.getCitiesByCountryListOid(countriesOIDs)).thenReturn(this.createResponseContainerCity
         * (1)); Long[] citiesOIDs = {1L};
         * when(this.clientMock.getCountriesByCityListOid(citiesOIDs)).thenReturn(this.createResponseContainerCountry
         * (1)); this.instance.initSnapshot(); Long[] list = new Long[15]; for (Integer i = 0; i < 15; i++) { list[i] =
         * Long.parseLong(i.toString()); }
         * 
         * verify(this.clientMock, new Times(1)).getCitiesByCountryListOid(list); verify(this.clientMock, new
         * Times(1)).getCountriesByCityListOid(list);
         * 
         * this.instance.deleteSnapshot();
         * 
         * Assert.assertEquals( this.instance.getCities(1L), "1"); Assert.assertEquals( this.instance.getCountry(1L),
         * "1");
         */


    }

    private ResponseContainer<Map<Long, CountryDTO>> createResponseContainerCountry(Integer count) {
        ResponseContainer<Map<Long, CountryDTO>> responseContainer = new ResponseContainer<Map<Long, CountryDTO>>();
        Map<Long, CountryDTO> data = new HashMap<Long, CountryDTO>();
        for (Long i = 0L; i < count; i++) {
            CountryDTO value = new CountryDTO();
            value.setOID(i);
            data.put(i, value);
            responseContainer.setData(data);
        }
        return responseContainer;
    }

    private ResponseContainer<Map<Long, Collection<CityDTO>>> createResponseContainerCity(Integer countCountry) {
        ResponseContainer<Map<Long, Collection<CityDTO>>> responseContainer = new ResponseContainer<Map<Long, Collection<CityDTO>>>();
        Map<Long, Collection<CityDTO>> data = new HashMap<Long, Collection<CityDTO>>();
        for (Long i = 0L; i < countCountry; i++) {

            List<CityDTO> cities = new LinkedList<CityDTO>();
            for (Long j = 0L; j < 15; j++) {
                CityDTO value = new CityDTO();
                value.setOID(j);
                cities.add(value);
            }
            data.put(i, cities);
            responseContainer.setData(data);
        }
        return responseContainer;
    }

    private List<Long> createGeoAreaDAO() {

        List<Long> listOIDs = new LinkedList<Long>();
        for (Long i = 0L; i < 15; i++) {
            listOIDs.add(i);
        }
        return listOIDs;

    }
}
