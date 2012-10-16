package com.despegar.sobek.service.impl;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.despegar.framework.caching.CacheTemplate;
import com.despegar.framework.caching.repository.impl.MockedCacheRepositoryManager;
import com.despegar.library.rest.connector.exceptions.ServiceException;
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
import com.despegar.sobek.translator.CatalogueTranslator;
import com.google.common.collect.Lists;

public class CatalogueServiceImplTest {

	private CatalogueServiceImpl instance = new CatalogueServiceImpl();
	@Mock
	private BenefitCategoryDAO benefitCategoryDAOMock;

	@Mock
	private BenefitStatusDAO benefitStatusDAOMock;

	@Mock
	private BrandDAO brandMock;

	@Mock
	private ProductDAO productMock;

	@Mock
	private CatalogueTranslator catalogueTranslatorMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		this.instance.setBenefitCategoryDAO(this.benefitCategoryDAOMock);
		this.instance.setBenefitStatusDAO(this.benefitStatusDAOMock);
		this.instance.setBrandDAO(brandMock);
		this.instance.setProductDAO(productMock);

		CacheTemplate cacheTemplate = new CacheTemplate();
		cacheTemplate.setCacheRepositoryManager(new MockedCacheRepositoryManager());
		this.instance.setCacheTemplate(cacheTemplate);
		this.instance.setCatalogueTranslator(catalogueTranslatorMock);
	}

	/*
	 * Benefit Category
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void getAllBenefitCategoriesByLanguage_correct_returnsCollectionCatalogueDTO() {

		String languageCode = "ES";
		BenefitCategory benefitCategory = new BenefitCategory();
		benefitCategory.setCode(languageCode);
		List<BenefitCategory> benefitCategories = Lists.newArrayList(benefitCategory);

		CatalogueDTO catalogueDTO = new CatalogueDTO();
		catalogueDTO.setCode(languageCode);

		when(this.benefitCategoryDAOMock.getAllBenefitCategoriesByLanguage(any(String.class))).thenReturn(
				benefitCategories);
		when(this.catalogueTranslatorMock.getDTO(any(AbstractCatalogue.class))).thenReturn(catalogueDTO);

		Collection<CatalogueDTO> sobekDTOs = this.instance.getAllBenefitCategoriesByLanguage(languageCode);

		verify(this.catalogueTranslatorMock).getDTO(any(AbstractCatalogue.class));
		verify(this.benefitCategoryDAOMock).getAllBenefitCategoriesByLanguage(any(String.class));

		assertNotNull(sobekDTOs);
		assertEquals(benefitCategories.size(), sobekDTOs.size());

	}

	@SuppressWarnings("unchecked")
	@Test
	public void getAllBenefitCategoriesByLanguage_nonExistentLanguageCode_returnsEmptyCollection() {

		List<BenefitCategory> emptyBenefitCategories = Lists.newArrayList();
		when(this.benefitCategoryDAOMock.getAllBenefitCategoriesByLanguage(any(String.class))).thenReturn(
				emptyBenefitCategories);

		Collection<CatalogueDTO> sobekDTOs = this.instance.getAllBenefitCategoriesByLanguage("SOB");

		verify(this.benefitCategoryDAOMock).getAllBenefitCategoriesByLanguage(any(String.class));
		verify(this.catalogueTranslatorMock, never()).getDTO(any(AbstractCatalogue.class));

		assertNotNull(sobekDTOs);
		assertEquals(0, sobekDTOs.size());
	}

	@SuppressWarnings("unchecked")
	@Test(expected = ServiceException.class)
	public void getAllBenefitCategoriesByLanguage_nullLanguageCode_throwsServiceException() {

		try {
			this.instance.getAllBenefitCategoriesByLanguage(null);
		} catch (ServiceException se) {
			verify(this.benefitCategoryDAOMock, never()).getAllBenefitCategoriesByLanguage(any(String.class));
			verify(this.catalogueTranslatorMock, never()).getDTO(any(AbstractCatalogue.class));
			throw se;
		}

	}

	@SuppressWarnings("unchecked")
	@Test(expected = ServiceException.class)
	public void getAllBenefitCategoriesByLanguage_emptyLanguageCode_throwsServiceException() {

		try {
			this.instance.getAllBenefitCategoriesByLanguage("");
		} catch (ServiceException se) {
			verify(this.benefitCategoryDAOMock, never()).getAllBenefitCategoriesByLanguage(any(String.class));
			verify(this.catalogueTranslatorMock, never()).getDTO(any(AbstractCatalogue.class));
			throw se;
		}

	}

	/*
	 * Benefit Status
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void getAllBenefitStatusByLanguage_correct_returnsCollectionCatalogueDTO() {

		String languageCode = "ES";
		BenefitStatus bStatus = new BenefitStatus();
		bStatus.setCode(languageCode);
		List<BenefitStatus> benefitStatus = Lists.newArrayList(bStatus);

		CatalogueDTO catalogueDTO = new CatalogueDTO();
		catalogueDTO.setCode(languageCode);

		when(this.benefitStatusDAOMock.getAllBenefitStatusByLanguage(any(String.class))).thenReturn(benefitStatus);
		when(this.catalogueTranslatorMock.getDTO(any(AbstractCatalogue.class))).thenReturn(catalogueDTO);

		Collection<CatalogueDTO> benefitStatusDTOs = this.instance.getAllBenefitStatusByLanguage(languageCode);

		verify(this.catalogueTranslatorMock).getDTO(any(AbstractCatalogue.class));
		verify(this.benefitStatusDAOMock).getAllBenefitStatusByLanguage(any(String.class));

		assertNotNull(benefitStatusDTOs);
		assertEquals(benefitStatus.size(), benefitStatusDTOs.size());

	}

	@SuppressWarnings("unchecked")
	@Test
	public void getAllBenefitStatusByLanguage_nonExistentLanguageCode_returnsEmptyCollection() {

		List<BenefitStatus> emptyBenefitStatus = Lists.newArrayList();
		when(this.benefitStatusDAOMock.getAllBenefitStatusByLanguage(any(String.class))).thenReturn(emptyBenefitStatus);

		Collection<CatalogueDTO> benefitStatusDTOs = this.instance.getAllBenefitStatusByLanguage("SOB");

		verify(this.benefitStatusDAOMock).getAllBenefitStatusByLanguage(any(String.class));
		verify(this.catalogueTranslatorMock, never()).getDTO(any(AbstractCatalogue.class));

		assertNotNull(benefitStatusDTOs);
		assertEquals(0, benefitStatusDTOs.size());
	}

	@SuppressWarnings("unchecked")
	@Test(expected = ServiceException.class)
	public void getAllBenefitStatusByLanguage_nullLanguageCode_throwsServiceException() {

		try {
			this.instance.getAllBenefitCategoriesByLanguage(null);
		} catch (ServiceException se) {
			verify(this.benefitStatusDAOMock, never()).getAllBenefitStatusByLanguage(any(String.class));
			verify(this.catalogueTranslatorMock, never()).getDTO(any(AbstractCatalogue.class));
			throw se;
		}

	}

	@SuppressWarnings("unchecked")
	@Test(expected = ServiceException.class)
	public void getAllBenefitStatusByLanguage_emptyLanguageCode_throwsServiceException() {

		try {
			this.instance.getAllBenefitCategoriesByLanguage("");
		} catch (ServiceException se) {
			verify(this.benefitStatusDAOMock, never()).getAllBenefitStatusByLanguage(any(String.class));
			verify(this.catalogueTranslatorMock, never()).getDTO(any(AbstractCatalogue.class));
			throw se;
		}

	}

	/*
	 * Brand
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void getAllBrandsByLanguage_correct_returnsCollectionCatalogueDTO() {

		String languageCode = "ES";
		Brand brand = new Brand();
		brand.setCode(languageCode);
		List<Brand> brands = Lists.newArrayList(brand);

		CatalogueDTO catalogueDTO = new CatalogueDTO();
		catalogueDTO.setCode(languageCode);

		when(this.brandMock.getAllBrandsByLanguage(any(String.class))).thenReturn(brands);
		when(this.catalogueTranslatorMock.getDTO(any(AbstractCatalogue.class))).thenReturn(catalogueDTO);

		Collection<CatalogueDTO> brandDTOs = this.instance.getAllBrandsByLanguage(languageCode);

		verify(this.catalogueTranslatorMock).getDTO(any(AbstractCatalogue.class));
		verify(this.brandMock).getAllBrandsByLanguage(any(String.class));

		assertNotNull(brandDTOs);
		assertEquals(brands.size(), brandDTOs.size());

	}

	@SuppressWarnings("unchecked")
	@Test
	public void getAllBrandsByLanguage_nonExistentLanguageCode_returnsEmptyCollection() {

		List<Brand> emptyBrands = Lists.newArrayList();
		when(this.brandMock.getAllBrandsByLanguage(any(String.class))).thenReturn(emptyBrands);

		Collection<CatalogueDTO> brandDTOs = this.instance.getAllBrandsByLanguage("SOB");

		verify(this.brandMock).getAllBrandsByLanguage(any(String.class));
		verify(this.catalogueTranslatorMock, never()).getDTO(any(AbstractCatalogue.class));

		assertNotNull(brandDTOs);
		assertEquals(0, brandDTOs.size());
	}

	@SuppressWarnings("unchecked")
	@Test(expected = ServiceException.class)
	public void getAllBrandsByLanguage_nullLanguageCode_throwsServiceException() {

		try {
			this.instance.getAllBrandsByLanguage(null);
		} catch (ServiceException se) {
			verify(this.brandMock, never()).getAllBrandsByLanguage(any(String.class));
			verify(this.catalogueTranslatorMock, never()).getDTO(any(AbstractCatalogue.class));
			throw se;
		}

	}

	@SuppressWarnings("unchecked")
	@Test(expected = ServiceException.class)
	public void getAllBrandsByLanguage_emptyLanguageCode_throwsServiceException() {

		try {
			this.instance.getAllBrandsByLanguage("");
		} catch (ServiceException se) {
			verify(this.brandMock, never()).getAllBrandsByLanguage(any(String.class));
			verify(this.catalogueTranslatorMock, never()).getDTO(any(AbstractCatalogue.class));
			throw se;
		}

	}

	/*
	 * Product
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void getAllProductByLanguage_correct_returnsCollectionCatalogueDTO() {

		String languageCode = "ES";
		Product product = new Product();
		product.setCode(languageCode);
		List<Product> products = Lists.newArrayList(product);

		CatalogueDTO catalogueDTO = new CatalogueDTO();
		catalogueDTO.setCode(languageCode);

		when(this.productMock.getAllProductsByLanguage(any(String.class))).thenReturn(products);
		when(this.catalogueTranslatorMock.getDTO(any(AbstractCatalogue.class))).thenReturn(catalogueDTO);

		Collection<CatalogueDTO> productsDTOs = this.instance.getAllProductsByLanguage(languageCode);

		verify(this.catalogueTranslatorMock).getDTO(any(AbstractCatalogue.class));
		verify(this.productMock).getAllProductsByLanguage(any(String.class));

		assertNotNull(productsDTOs);
		assertEquals(products.size(), productsDTOs.size());

	}

	@SuppressWarnings("unchecked")
	@Test
	public void getAllProductsByLanguage_nonExistentLanguageCode_returnsEmptyCollection() {

		List<Product> emptyProducts = Lists.newArrayList();
		when(this.productMock.getAllProductsByLanguage(any(String.class))).thenReturn(emptyProducts);

		Collection<CatalogueDTO> productDTOs = this.instance.getAllProductsByLanguage("SOB");

		verify(this.productMock).getAllProductsByLanguage(any(String.class));
		verify(this.catalogueTranslatorMock, never()).getDTO(any(AbstractCatalogue.class));

		assertNotNull(productDTOs);
		assertEquals(0, productDTOs.size());
	}

	@SuppressWarnings("unchecked")
	@Test(expected = ServiceException.class)
	public void getAllProductByLanguage_nullLanguageCode_throwsServiceException() {

		try {
			this.instance.getAllProductsByLanguage(null);
		} catch (ServiceException se) {
			verify(this.productMock, never()).getAllProductsByLanguage(any(String.class));
			verify(this.catalogueTranslatorMock, never()).getDTO(any(AbstractCatalogue.class));
			throw se;
		}

	}

	@SuppressWarnings("unchecked")
	@Test(expected = ServiceException.class)
	public void getAllProductsByLanguage_emptyLanguageCode_throwsServiceException() {

		try {
			this.instance.getAllProductsByLanguage("");
		} catch (ServiceException se) {
			verify(this.productMock, never()).getAllProductsByLanguage(any(String.class));
			verify(this.catalogueTranslatorMock, never()).getDTO(any(AbstractCatalogue.class));
			throw se;
		}

	}

	/*
	 * Full Catalogue
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void getFullCatalogue_correct_returnsCollectionCatalogueDTO() {

		String languageCode = "ES";
		CatalogueDTO catalogueDTO = new CatalogueDTO();
		catalogueDTO.setCode(languageCode);

		Product product = new Product();
		product.setCode(languageCode);
		List<Product> products = Lists.newArrayList(product);

		Brand brand = new Brand();
		brand.setCode(languageCode);
		List<Brand> brands = Lists.newArrayList(brand);

		BenefitStatus bStatus = new BenefitStatus();
		bStatus.setCode(languageCode);
		List<BenefitStatus> benefitStatus = Lists.newArrayList(bStatus);

		BenefitCategory benefitCategory = new BenefitCategory();
		benefitCategory.setCode(languageCode);
		List<BenefitCategory> benefitCategories = Lists.newArrayList(benefitCategory);

		when(this.benefitCategoryDAOMock.getAllBenefitCategoriesByLanguage(any(String.class))).thenReturn(
				benefitCategories);
		when(this.benefitStatusDAOMock.getAllBenefitStatusByLanguage(any(String.class))).thenReturn(benefitStatus);
		when(this.brandMock.getAllBrandsByLanguage(any(String.class))).thenReturn(brands);
		when(this.productMock.getAllProductsByLanguage(any(String.class))).thenReturn(products);
		when(this.catalogueTranslatorMock.getDTO(any(AbstractCatalogue.class))).thenReturn(catalogueDTO);

		FullCatalogueDTO fullCatalogueDTO = this.instance.getFullCatalogueByLanguage(languageCode);

		verify(this.catalogueTranslatorMock, times(4)).getDTO(any(AbstractCatalogue.class));
		verify(this.productMock).getAllProductsByLanguage(any(String.class));
		verify(this.benefitCategoryDAOMock).getAllBenefitCategoriesByLanguage(any(String.class));
		verify(this.benefitStatusDAOMock).getAllBenefitStatusByLanguage(any(String.class));
		verify(this.brandMock).getAllBrandsByLanguage(any(String.class));

		assertNotNull(fullCatalogueDTO);
		assertEquals(products.size(), fullCatalogueDTO.getProducts().size());
		assertEquals(benefitCategories.size(), fullCatalogueDTO.getBenefitCategories().size());
		assertEquals(benefitStatus.size(), fullCatalogueDTO.getBenefitStatus().size());
		assertEquals(brands.size(), fullCatalogueDTO.getBrands().size());

	}

}
