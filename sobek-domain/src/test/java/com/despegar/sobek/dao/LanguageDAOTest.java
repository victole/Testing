package com.despegar.sobek.dao;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import com.despegar.framework.persistence.hibernate.dao.ReadWriteObjectDAO;
import com.despegar.framework.tests.spring.AbstractTransactionalSpringTest;
import com.despegar.sobek.model.Language;

@ContextConfiguration(locations = { "classpath:com/despegar/test/test-reference-data-context.xml" })
public class LanguageDAOTest extends AbstractTransactionalSpringTest {

	@Autowired
	private LanguageDAO instance;

	@Autowired
	private ReadWriteObjectDAO<Language> languageReadWriteDAO;

	@Before
	public void setUp() {

		Language esLang = new Language();
		esLang.setIsoCode("EST");
		esLang.setName("Español");
		this.languageReadWriteDAO.save(esLang);

		Language brLang = new Language();
		brLang.setIsoCode("BRT");
		brLang.setName("Portuguese");
		this.languageReadWriteDAO.save(brLang);

	}

	@Test
	public void findByIsoCode_correct_returnsLanguage() {
		Language language = instance.findByIsoCode("EST");
		assertNotNull(language);
		assertEquals("EST", language.getIsoCode());
		assertEquals("Español", language.getName());
	}

	@Test
	public void findByIsoCode_unknownIsoCode_returnsNull() {
		Language language = instance.findByIsoCode("PST");
		assertNull(language);
	}
}
