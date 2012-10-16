package com.despegar.sobek.dao;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import java.util.Collection;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import com.despegar.framework.persistence.hibernate.dao.ReadWriteObjectDAO;
import com.despegar.framework.tests.spring.AbstractTransactionalSpringTest;
import com.despegar.sobek.model.Brand;
import com.despegar.sobek.model.BrandDescriptionI18N;
import com.despegar.sobek.model.Language;
import com.google.common.collect.Sets;

@ContextConfiguration(locations = { "classpath:com/despegar/test/test-reference-data-context.xml" })
public class BrandDAOTest extends AbstractTransactionalSpringTest {

	@Autowired
	private BrandDAO instance;
	@Autowired
	private ReadWriteObjectDAO<Brand> brandReadWriteDAO;
	@Autowired
	private ReadWriteObjectDAO<Language> languageReadWriteDAO;

	@Before
	public void setUp() {

		Language esLang = new Language();
		esLang.setIsoCode("EST");
		esLang.setName("Español");
		this.languageReadWriteDAO.save(esLang);

		BrandDescriptionI18N esDescription = new BrandDescriptionI18N();
		esDescription.setDescription("Despegar Argentina");
		esDescription.setLanguage(esLang);

		Set<BrandDescriptionI18N> descriptions = Sets.newHashSet(esDescription);
		Brand brand = new Brand();
		brand.setCode("DESP_AR");
		brand.setDescriptions(descriptions);
		brandReadWriteDAO.save(brand);
	}

	@Test
	public void getAllBrandsByLanguage_correct_returnBrandCollection() {
		String languageCode = "EST";
		Collection<Brand> allBrandsByLanguage = this.instance.getAllBrandsByLanguage(languageCode);
		assertEquals(1, allBrandsByLanguage.size());
		Brand brand = allBrandsByLanguage.iterator().next();
		assertEquals("DESP_AR", brand.getCode());
		assertEquals(1, brand.getDescriptions().size());
		BrandDescriptionI18N description = brand.getDescriptions().iterator().next();
		assertEquals("Despegar Argentina", description.getDescription());
		assertEquals(languageCode, description.getLanguage().getIsoCode());
	}

	@Test
	public void getAllBrandsByLanguage_incorrectLanguage_returnBrandCollection() {
		String languageCode = "UNK";
		Collection<Brand> allBrandsByLanguage = this.instance.getAllBrandsByLanguage(languageCode);
		assertEquals(0, allBrandsByLanguage.size());
	}

	@Test
	public void findByCode_code_returnsBrand() {

		Brand brand = instance.findByCode("DESP_AR");

		assertNotNull(brand);
		assertEquals(1, brand.getDescriptions().size());
		assertEquals("DESP_AR", brand.getCode());

	}
}
