package com.despegar.sobek.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.despegar.framework.persistence.AbstractIdentifiablePersistentObjectWithLogicalDeletion;

@Entity
@Table(name = "PICTURE_SIZE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, include = "all", region = "com.despegar.sobek.model.PictureSize")
public class PictureSize extends AbstractIdentifiablePersistentObjectWithLogicalDeletion {

	private static final long serialVersionUID = 1L;
	private String name;
	private Integer width;
	private Integer height;

	@Column(name = "NAME", unique = true, nullable = false)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "WIDTH")
	public Integer getWidth() {
		return this.width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	@Column(name = "HEIGHT")
	public Integer getHeight() {
		return this.height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

}
