package com.despegar.sobek.dao;

import java.util.List;

import com.despegar.sobek.model.Brand;

public class BrandDAO extends AbstractCatalogueDAO<Brand> {

	public List<Brand> getAllBrandsByLanguage(String languageCode) {
		return this.getAllCataloguesByLanguage(languageCode);
	}

}
