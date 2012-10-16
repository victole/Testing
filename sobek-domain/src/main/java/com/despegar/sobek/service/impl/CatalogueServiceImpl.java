package com.despegar.sobek.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import com.despegar.framework.caching.CacheTemplate;
import com.despegar.framework.caching.SingleCacheableExecutionBlock;
import com.despegar.library.rest.connector.exceptions.ServiceError;
import com.despegar.library.rest.connector.exceptions.ServiceException;
import com.despegar.framework.utils.collection.SerializableCollection;
import com.despegar.framework.utils.collection.impl.SerializableCollectionImpl;
import com.despegar.framework.utils.string.StringUtils;
import com.despegar.sobek.dao.BenefitCategoryDAO;
import com.despegar.sobek.dao.BenefitStatusDAO;
import com.despegar.sobek.dao.BrandDAO;
import com.despegar.sobek.dao.ProductDAO;
import com.despegar.sobek.dto.CatalogueDTO;
import com.despegar.sobek.dto.FullCatalogueDTO;
import com.despegar.sobek.model.AbstractCatalogue;
import com.despegar.sobek.model.BenefitCategory;
import com.despegar.sobek.model.BenefitStatus;
import com.despegar.sobek.model.Brand;
import com.despegar.sobek.model.Product;
import com.despegar.sobek.service.CatalogueService;
import com.despegar.sobek.translator.CatalogueTranslator;
import com.google.common.collect.Lists;

public class CatalogueServiceImpl implements CatalogueService {

	private static final Logger logger = Logger.getLogger(CatalogueServiceImpl.class);

	private BenefitCategoryDAO benefitCategoryDAO;
	private BenefitStatusDAO benefitStatusDAO;
	private ProductDAO productDAO;
	private BrandDAO brandDAO;

	private CatalogueTranslator catalogueTranslator;
	private CacheTemplate cacheTemplate;

	@Override
	public Collection<CatalogueDTO> getAllBenefitCategoriesByLanguage(String languageCode) {

		logger.info(StringUtils.concat("Retrieving BenefitCategories for languageCode:", languageCode));

		this.validateCatalogueParams(languageCode);

		Collection<CatalogueDTO> benefitCategoryDTOs = this.cacheTemplate.execute(languageCode,
				"com.despegar.sobek.service.impl.CatalogueServiceImpl.getAllBenefitCategoriesByLanguage()",
				new SingleCacheableExecutionBlock<String, SerializableCollection<CatalogueDTO>>() {
					@Override
					public SerializableCollection<CatalogueDTO> execute(String languageCode) {
						Collection<BenefitCategory> benefitCategories = benefitCategoryDAO
								.getAllBenefitCategoriesByLanguage(languageCode);

						return transformToCatalogueDTOCollection(benefitCategories);
					}

				}).asCollection();

		logger.info(StringUtils.concat("Returning ", benefitCategoryDTOs.size(), " Benefit Categories"));

		return benefitCategoryDTOs;
	}

	@Override
	public Collection<CatalogueDTO> getAllBrandsByLanguage(String languageCode) {

		logger.info(StringUtils.concat("Retrieving Brands for languageCode:", languageCode));

		this.validateCatalogueParams(languageCode);

		Collection<CatalogueDTO> brandsDTOs = this.cacheTemplate.execute(languageCode,
				"com.despegar.sobek.service.impl.CatalogueServiceImpl.getAllBrandsByLanguage()",

				new SingleCacheableExecutionBlock<String, SerializableCollection<CatalogueDTO>>() {
					@Override
					public SerializableCollection<CatalogueDTO> execute(String languageCode) {
						Collection<Brand> brands = brandDAO.getAllBrandsByLanguage(languageCode);
						return transformToCatalogueDTOCollection(brands);
					}
				}).asCollection();

		logger.info(StringUtils.concat("Returning ", brandsDTOs.size(), " Brands"));

		return brandsDTOs;
	}

	@Override
	public Collection<CatalogueDTO> getAllBenefitStatusByLanguage(String languageCode) {
		logger.info(StringUtils.concat("Retrieving BenefitStatus for languageCode:", languageCode));

		this.validateCatalogueParams(languageCode);

		Collection<CatalogueDTO> benefitStatusDTOs = this.cacheTemplate.execute(languageCode,
				"com.despegar.sobek.service.impl.CatalogueServiceImpl.getAllBenefitStatusByLanguage()",

				new SingleCacheableExecutionBlock<String, SerializableCollection<CatalogueDTO>>() {
					@Override
					public SerializableCollection<CatalogueDTO> execute(String languageCode) {
						Collection<BenefitStatus> benefitStatus = benefitStatusDAO
								.getAllBenefitStatusByLanguage(languageCode);
						return transformToCatalogueDTOCollection(benefitStatus);
					}
				}).asCollection();

		logger.info(StringUtils.concat("Returning ", benefitStatusDTOs.size(), " Benefit Status"));

		return benefitStatusDTOs;
	}

	@Override
	public Collection<CatalogueDTO> getAllProductsByLanguage(String languageCode) {
		logger.info(StringUtils.concat("Retrieving Products for languageCode:", languageCode));

		this.validateCatalogueParams(languageCode);

		Collection<CatalogueDTO> productDTOs = this.cacheTemplate.execute(languageCode,
				"com.despegar.sobek.service.impl.CatalogueServiceImpl.getAllProductsByLanguage()",

				new SingleCacheableExecutionBlock<String, SerializableCollection<CatalogueDTO>>() {
					@Override
					public SerializableCollection<CatalogueDTO> execute(String languageCode) {
						Collection<Product> products = productDAO.getAllProductsByLanguage(languageCode);
						return transformToCatalogueDTOCollection(products);
					}
				}).asCollection();

		logger.info(StringUtils.concat("Returning ", productDTOs.size(), " Products"));

		return productDTOs;
	}

	public FullCatalogueDTO getFullCatalogueByLanguage(String languageCode) {
		logger.info(StringUtils.concat("Retrieving Products for languageCode:", languageCode));

		this.validateCatalogueParams(languageCode);

		FullCatalogueDTO fullCatalogueDTO = this.cacheTemplate.execute(languageCode,
				"com.despegar.sobek.service.impl.CatalogueServiceImpl.getFullCatalogueByLanguage()",

				new SingleCacheableExecutionBlock<String, FullCatalogueDTO>() {
					@Override
					public FullCatalogueDTO execute(String languageCode) {

						Collection<CatalogueDTO> products = getAllProductsByLanguage(languageCode);
						Collection<CatalogueDTO> benefitStatus = getAllBenefitStatusByLanguage(languageCode);
						Collection<CatalogueDTO> benefitCategories = getAllBenefitCategoriesByLanguage(languageCode);
						Collection<CatalogueDTO> brands = getAllBrandsByLanguage(languageCode);

						FullCatalogueDTO fullCatalogueDTO = new FullCatalogueDTO();
						fullCatalogueDTO.setBenefitCategories(benefitCategories);
						fullCatalogueDTO.setBenefitStatus(benefitStatus);
						fullCatalogueDTO.setBrands(brands);
						fullCatalogueDTO.setProducts(products);

						return fullCatalogueDTO;
					}
				});

		int benefitCategoriesCount = fullCatalogueDTO.getBenefitCategories().size();
		int benefitStatusCount = fullCatalogueDTO.getBenefitStatus().size();
		int brandsCount = fullCatalogueDTO.getBrands().size();
		int productsCount = fullCatalogueDTO.getProducts().size();

		logger.info(StringUtils.concat("Returning full catalogue [benefitCategories]: ", benefitCategoriesCount,
				" [benefitStatus]:", benefitStatusCount, " [brandsCount]:", brandsCount, " [productsCount]:",
				productsCount));

		return fullCatalogueDTO;
	}

	private <T extends AbstractCatalogue<?>> List<CatalogueDTO> transformToCatalogueDTOList(Collection<T> catalogues) {
		List<CatalogueDTO> catalogueDTOs = new ArrayList<CatalogueDTO>();
		for (T catalogue : catalogues) {
			catalogueDTOs.add(catalogueTranslator.getDTO(catalogue));
		}
		return catalogueDTOs;
	}

	private <T extends AbstractCatalogue<?>> SerializableCollection<CatalogueDTO> transformToCatalogueDTOCollection(
			Collection<T> catalogues) {

		List<CatalogueDTO> catalogueDTOs = this.transformToCatalogueDTOList(catalogues);
		return new SerializableCollectionImpl<CatalogueDTO>(catalogueDTOs);
	}

	private void validateCatalogueParams(String languageCode) {
		if (languageCode == null || languageCode.isEmpty()) {
			ServiceError serviceError = new ServiceError("Language Code cannot be null or empty");
			List<ServiceError> errors = Lists.newArrayList(serviceError);
			throw new ServiceException(errors);
		}
	}

	public void setBenefitCategoryDAO(BenefitCategoryDAO benefitCategoryDAO) {
		this.benefitCategoryDAO = benefitCategoryDAO;
	}

	public void setBenefitStatusDAO(BenefitStatusDAO benefitStatusDAO) {
		this.benefitStatusDAO = benefitStatusDAO;
	}

	public void setProductDAO(ProductDAO productDAO) {
		this.productDAO = productDAO;
	}

	public void setBrandDAO(BrandDAO brandDAO) {
		this.brandDAO = brandDAO;
	}

	public void setCacheTemplate(CacheTemplate cacheTemplate) {
		this.cacheTemplate = cacheTemplate;
	}

	public void setCatalogueTranslator(CatalogueTranslator catalogueTranslator) {
		this.catalogueTranslator = catalogueTranslator;
	}
}
