package com.despegar.sobek.dao;

import java.util.List;

import com.despegar.framework.persistence.hibernate.dao.generic.AbstractReadWriteObjectDAO;
import com.despegar.framework.persistence.hibernate.dao.generic.query.ProjectedQueryDefinition;
import com.despegar.framework.persistence.hibernate.dao.generic.query.QueryDefinition;
import com.despegar.sobek.model.GeoArea;

public class GeoAreaDAO extends AbstractReadWriteObjectDAO<GeoArea> {

	public List<Long> getGeoAreaOIDs(String type) {
		ProjectedQueryDefinition<Long> projectedQueryDefinition = new ProjectedQueryDefinition<Long>();
		QueryDefinition queryDefinition = new QueryDefinition();
		queryDefinition.addProperty("type", type);
		projectedQueryDefinition.addProjectedProperty("despegarItemOID");
		projectedQueryDefinition.setQueryDefinition(queryDefinition);
		List<Long> results = this.findByProjectedQueryDefinition(projectedQueryDefinition);
		return results;
	}

	public GeoArea getGeoAreaByItemOID(Long itemOID) {
		return this.findUnique("despegarItemOID", itemOID);
	}

}
