package com.despegar.sobek.service;

import java.util.Collection;

import com.despegar.sobek.dto.CatalogueDTO;
import com.despegar.sobek.dto.FullCatalogueDTO;

public interface CatalogueService {

	public Collection<CatalogueDTO> getAllBenefitCategoriesByLanguage(String languageCode);

	public Collection<CatalogueDTO> getAllBenefitStatusByLanguage(String languageCode);

	public Collection<CatalogueDTO> getAllBrandsByLanguage(String languageCode);

	public Collection<CatalogueDTO> getAllProductsByLanguage(String languageCode);

	public FullCatalogueDTO getFullCatalogueByLanguage(String languageCode);

}
