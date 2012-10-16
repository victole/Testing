package com.despegar.sobek.dao;

import java.util.List;

import com.despegar.sobek.model.BenefitCategory;

public class BenefitCategoryDAO extends AbstractCatalogueDAO<BenefitCategory> {

	public List<BenefitCategory> getAllBenefitCategoriesByLanguage(String languageCode) {
		return this.getAllCataloguesByLanguage(languageCode);
	}
}
