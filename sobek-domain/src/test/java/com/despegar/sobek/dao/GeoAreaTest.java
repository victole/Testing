package com.despegar.sobek.dao;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import com.despegar.framework.persistence.hibernate.dao.generic.AbstractReadWriteObjectDAO;
import com.despegar.framework.tests.spring.AbstractTransactionalSpringTest;
import com.despegar.sobek.model.GeoArea;

@ContextConfiguration(locations = { "classpath:com/despegar/test/test-reference-data-context.xml" })
public class GeoAreaTest extends AbstractTransactionalSpringTest {

	@Autowired
	private GeoAreaDAO instance;

	@Autowired
	private AbstractReadWriteObjectDAO<GeoArea> geoAreaReadWriteDAO;

	private Long oid;

	@Before
	public void setUp() {
		oid = System.currentTimeMillis();
		GeoArea geoArea1 = new GeoArea();
		geoArea1.setDespegarItemOID(oid);
		geoArea1.setType("W_");

		GeoArea geoArea2 = new GeoArea();
		geoArea2.setDespegarItemOID(oid + 1);
		geoArea2.setType("W_");

		GeoArea geoArea3 = new GeoArea();
		geoArea3.setDespegarItemOID(oid + 2);
		geoArea3.setType("W_");

		this.geoAreaReadWriteDAO.save(geoArea1);
		this.geoAreaReadWriteDAO.save(geoArea2);
		this.geoAreaReadWriteDAO.save(geoArea3);
	}

	@Test
	public void getGeoAreaOIDs_correct_returnListOIDs() {

		List<Long> geoAreaOIDs = this.instance.getGeoAreaOIDs("W_");

		assertNotNull(geoAreaOIDs);
		assertFalse(geoAreaOIDs.isEmpty());
		assertEquals(3, geoAreaOIDs.size());

	}

	@Test
	public void getGeoAreaOIDs_unknownGeoAreaType_returnListOIDs() {

		List<Long> geoAreaOIDs = this.instance.getGeoAreaOIDs("Z_");

		assertNotNull(geoAreaOIDs);
		assertTrue(geoAreaOIDs.isEmpty());

	}

	@Test
	public void getGeoAreaByItemOID_correct_returnGeoArea() {

		GeoArea geoArea = this.instance.getGeoAreaByItemOID(this.oid);

		assertNotNull(geoArea);
		assertEquals(this.oid, geoArea.getDespegarItemOID());
	}

}
