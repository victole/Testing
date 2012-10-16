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
import com.despegar.sobek.model.BenefitCategory;
import com.despegar.sobek.model.BenefitCategoryDescriptionI18N;
import com.despegar.sobek.model.Language;
import com.google.common.collect.Sets;

@ContextConfiguration(locations = { "classpath:com/despegar/test/test-reference-data-context.xml" })
public class BenefitCategoryDAOTest extends AbstractTransactionalSpringTest {

	@Autowired
	private BenefitCategoryDAO instance;
	@Autowired
	private ReadWriteObjectDAO<BenefitCategory> benefitCategoryReadWriteDAO;
	@Autowired
	private ReadWriteObjectDAO<Language> languageReadWriteDAO;

	@Before
	public void setUp() {

		Language esLang = new Language();
		esLang.setIsoCode("EST");
		esLang.setName("Español");
		this.languageReadWriteDAO.save(esLang);

		BenefitCategoryDescriptionI18N esDescription = new BenefitCategoryDescriptionI18N();
		esDescription.setDescription("Restaurant_test");
		esDescription.setLanguage(esLang);

		Set<BenefitCategoryDescriptionI18N> descriptions = Sets.newHashSet(esDescription);
		BenefitCategory benefitCategory = new BenefitCategory();
		benefitCategory.setCode("RESTAURANT_TEST");
		benefitCategory.setDescriptions(descriptions);
		benefitCategoryReadWriteDAO.save(benefitCategory);
	}

	@Test
	public void getAllBenefitCategorysByLanguage_correct_returnBenefitCategoryCollection() {
		String languageCode = "EST";
		Collection<BenefitCategory> allBenefitCategorysByLanguage = this.instance
				.getAllBenefitCategoriesByLanguage(languageCode);
		assertEquals(1, allBenefitCategorysByLanguage.size());
		BenefitCategory brand = allBenefitCategorysByLanguage.iterator().next();
		assertEquals("RESTAURANT_TEST", brand.getCode());
		assertEquals(1, brand.getDescriptions().size());
		BenefitCategoryDescriptionI18N description = brand.getDescriptions().iterator().next();
		assertEquals("Restaurant_test", description.getDescription());
		assertEquals(languageCode, description.getLanguage().getIsoCode());
	}

	@Test
	public void getAllBenefitCategorysByLanguage_incorrectLanguage_returnBenefitCategoryCollection() {
		String languageCode = "UNK";
		Collection<BenefitCategory> allBenefitCategorysByLanguage = this.instance
				.getAllBenefitCategoriesByLanguage(languageCode);
		assertEquals(0, allBenefitCategorysByLanguage.size());
	}

	@Test
	public void findByCode_code_returnsBenefitCategory() {

		BenefitCategory category = instance.findByCode("RESTAURANT_TEST");

		assertNotNull(category);
		assertEquals(1, category.getDescriptions().size());
		assertEquals("RESTAURANT_TEST", category.getCode());

	}
}
