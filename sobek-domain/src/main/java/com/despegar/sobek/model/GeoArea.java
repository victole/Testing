package com.despegar.sobek.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.despegar.framework.persistence.AbstractIdentifiablePersistentObject;

@Entity
@Table(name = "GEO_AREA")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, include = "all")
public class GeoArea extends AbstractIdentifiablePersistentObject {

	private static final long serialVersionUID = 261887811665766388L;

	private String type;
	private Long despegarItemOID;

	@Column(name = "TYPE", nullable = false)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(name = "DESP_ITEM_OID", nullable = false, unique = true)
	public Long getDespegarItemOID() {
		return despegarItemOID;
	}

	public void setDespegarItemOID(Long despegarItemOID) {
		this.despegarItemOID = despegarItemOID;
	}

}