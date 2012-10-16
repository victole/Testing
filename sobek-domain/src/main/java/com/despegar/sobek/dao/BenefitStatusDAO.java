package com.despegar.sobek.dao;

import java.util.List;

import com.despegar.sobek.model.BenefitStatus;

public class BenefitStatusDAO extends AbstractCatalogueDAO<BenefitStatus> {

	public List<BenefitStatus> getAllBenefitStatusByLanguage(String languageCode) {
		return this.getAllCataloguesByLanguage(languageCode);
	}

}
