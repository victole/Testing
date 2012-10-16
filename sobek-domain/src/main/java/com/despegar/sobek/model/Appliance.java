package com.despegar.sobek.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.despegar.framework.persistence.AbstractIdentifiablePersistentObject;

@Entity
@Table(name = "APPLIANCE")
public class Appliance extends AbstractIdentifiablePersistentObject {

	private static final long serialVersionUID = 1L;
	private GeoArea destinationGeoArea;
	private GeoArea originGeoArea;
	private Brand brand;
	private Product product;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "ORIGIN_GEO_AREA_OID")
	@ForeignKey(name = "FK_APPLIANCE__ORIGIN_GEO_AREA")
	@Index(name = "IDX_APPLIANCE__ORIGIN_GEO_AREA_OID")
	public GeoArea getOriginGeoArea() {
		return originGeoArea;
	}

	public void setOriginGeoArea(GeoArea originGeoArea) {
		this.originGeoArea = originGeoArea;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "DESTINATION_GEO_AREA_OID")
	@ForeignKey(name = "FK_APPLIANCE__DESTINATION_GEO_AREA")
	@Index(name = "IDX_APPLIANCE__DESTINATION_GEO_AREA_OID")
	public GeoArea getDestinationGeoArea() {
		return destinationGeoArea;
	}

	public void setDestinationGeoArea(GeoArea destinationGeoArea) {
		this.destinationGeoArea = destinationGeoArea;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "BRAND_OID")
	@ForeignKey(name = "FK_APPLIANCE__BRAND")
	@Index(name = "IDX_APPLIANCE__BRAND_OID")
	public Brand getBrand() {
		return brand;
	}

	public void setBrand(Brand brand) {
		this.brand = brand;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "PRODUCT_OID")
	@ForeignKey(name = "FK_APPLIANCE__PRODUCT")
	@Index(name = "IDX_APPLIANCE__PRODUCT_OID")
	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}
}