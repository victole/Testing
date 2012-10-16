package com.despegar.sobek.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import com.despegar.framework.persistence.AbstractIdentifiablePersistentObject;

@MappedSuperclass
public abstract class AbstractResource extends AbstractIdentifiablePersistentObject {

	private static final long serialVersionUID = 7659110873785857052L;

	private String resourceName;

	@Column(name = "RESOURCE_NAME")
	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

}