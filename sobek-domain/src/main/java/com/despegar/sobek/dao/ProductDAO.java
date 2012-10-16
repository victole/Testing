package com.despegar.sobek.dao;

import java.util.List;

import com.despegar.sobek.model.Product;

public class ProductDAO extends AbstractCatalogueDAO<Product> {

	public List<Product> getAllProductsByLanguage(String languageCode) {
		return this.getAllCataloguesByLanguage(languageCode);
	}
}
