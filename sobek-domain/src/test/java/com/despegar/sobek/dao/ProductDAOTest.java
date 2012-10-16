package com.despegar.sobek.dao;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import com.despegar.framework.persistence.hibernate.dao.ReadWriteObjectDAO;
import com.despegar.framework.tests.spring.AbstractTransactionalSpringTest;
import com.despegar.sobek.model.Language;
import com.despegar.sobek.model.Product;
import com.despegar.sobek.model.ProductDescriptionI18N;
import com.google.common.collect.Sets;

@ContextConfiguration(locations = { "classpath:com/despegar/test/test-reference-data-context.xml" })
public class ProductDAOTest extends AbstractTransactionalSpringTest {

	@Autowired
	private ProductDAO instance;
	@Autowired
	private ReadWriteObjectDAO<Product> productReadWriteDAO;
	@Autowired
	private ReadWriteObjectDAO<Language> languageReadWriteDAO;

	@Before
	public void setUp() {

		Language esLang = new Language();
		esLang.setIsoCode("EST");
		esLang.setName("Español");
		this.languageReadWriteDAO.save(esLang);

		ProductDescriptionI18N esDescription = new ProductDescriptionI18N();
		esDescription.setDescription("vuelos_test");
		esDescription.setLanguage(esLang);

		Set<ProductDescriptionI18N> descriptions = Sets.newHashSet(esDescription);
		Product product = new Product();
		product.setCode("FLIGHTS_TEST");
		product.setDescriptions(descriptions);
		productReadWriteDAO.save(product);
	}

	@Test
	public void findByCode_code_returnsProduct() {

		Product product = instance.findByCode("FLIGHTS_TEST");

		assertNotNull(product);
		assertEquals(1, product.getDescriptions().size());
		assertEquals("FLIGHTS_TEST", product.getCode());

	}

	@Test
	public void getAllBrandsByLanguage_correct_returnBrandCollection() {
		String languageCode = "EST";
		List<Product> allBrandsByLanguage = this.instance.getAllProductsByLanguage(languageCode);
		assertEquals(1, allBrandsByLanguage.size());
		Product product = allBrandsByLanguage.iterator().next();
		assertEquals("FLIGHTS_TEST", product.getCode());
		assertEquals(1, product.getDescriptions().size());
		ProductDescriptionI18N description = product.getDescriptions().iterator().next();
		assertEquals("vuelos_test", description.getDescription());
		assertEquals(languageCode, description.getLanguage().getIsoCode());
	}

	@Test
	public void getAllBrandsByLanguage_incorrectLanguage_returnBrandCollection() {
		String languageCode = "UNK";
		List<Product> allBrandsByLanguage = this.instance.getAllProductsByLanguage(languageCode);
		assertEquals(0, allBrandsByLanguage.size());
	}
}
