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
import com.despegar.sobek.model.BenefitStatus;
import com.despegar.sobek.model.BenefitStatusDescriptionI18N;
import com.despegar.sobek.model.Language;
import com.google.common.collect.Sets;

@ContextConfiguration(locations = { "classpath:com/despegar/test/test-reference-data-context.xml" })
public class BenefitStatusDAOTest extends AbstractTransactionalSpringTest {

	@Autowired
	private BenefitStatusDAO instance;
	@Autowired
	private ReadWriteObjectDAO<BenefitStatus> benefitCategoryReadWriteDAO;
	@Autowired
	private ReadWriteObjectDAO<Language> languageReadWriteDAO;

	@Before
	public void setUp() {

		Language esLang = new Language();
		esLang.setIsoCode("EST");
		esLang.setName("Español");
		this.languageReadWriteDAO.save(esLang);

		BenefitStatusDescriptionI18N esDescription = new BenefitStatusDescriptionI18N();
		esDescription.setDescription("Publicado_test");
		esDescription.setLanguage(esLang);

		Set<BenefitStatusDescriptionI18N> descriptions = Sets.newHashSet(esDescription);
		BenefitStatus benefitCategory = new BenefitStatus();
		benefitCategory.setCode("PUBLICADO_TEST");
		benefitCategory.setDescriptions(descriptions);
		benefitCategoryReadWriteDAO.save(benefitCategory);
	}

	@Test
	public void getAllBenefitStatussByLanguage_correct_returnBenefitStatusCollection() {
		String languageCode = "EST";
		Collection<BenefitStatus> allBenefitStatussByLanguage = this.instance
				.getAllBenefitStatusByLanguage(languageCode);
		assertEquals(1, allBenefitStatussByLanguage.size());
		BenefitStatus brand = allBenefitStatussByLanguage.iterator().next();
		assertEquals("PUBLICADO_TEST", brand.getCode());
		assertEquals(1, brand.getDescriptions().size());
		BenefitStatusDescriptionI18N description = brand.getDescriptions().iterator().next();
		assertEquals("Publicado_test", description.getDescription());
		assertEquals(languageCode, description.getLanguage().getIsoCode());
	}

	@Test
	public void getAllBenefitStatussByLanguage_incorrectLanguage_returnBenefitStatusCollection() {
		String languageCode = "UNK";
		Collection<BenefitStatus> allBenefitStatussByLanguage = this.instance
				.getAllBenefitStatusByLanguage(languageCode);
		assertEquals(0, allBenefitStatussByLanguage.size());
	}

	@Test
	public void findByCode_code_returnsBenefitStatus() {

		BenefitStatus status = instance.findByCode("PUBLICADO_TEST");

		assertNotNull(status);
		assertEquals(1, status.getDescriptions().size());
		assertEquals("PUBLICADO_TEST", status.getCode());

	}
}
