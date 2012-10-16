package com.despegar.sobek.dao;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import com.despegar.framework.persistence.hibernate.dao.ReadWriteObjectDAO;
import com.despegar.framework.tests.spring.AbstractTransactionalSpringTest;
import com.despegar.sobek.model.Benefit;
import com.despegar.sobek.model.BenefitCategory;
import com.despegar.sobek.model.BenefitStatus;
import com.despegar.sobek.model.BenefitStatusCode;
import com.despegar.sobek.model.Brand;
import com.despegar.sobek.model.Company;
import com.despegar.sobek.model.GeoArea;
import com.despegar.sobek.model.Language;
import com.despegar.sobek.model.Product;
import com.despegar.sobek.utility.ModelContentCreator;

@ContextConfiguration(locations = { "classpath:com/despegar/test/test-reference-data-context.xml" })
public class BenefitDAOTest extends AbstractTransactionalSpringTest {

	@Autowired
	private BenefitDAO benefitDAO;
	@Autowired
	private ReadWriteObjectDAO<Language> languageReadWriteDAO;
	@Autowired
	private ReadWriteObjectDAO<BenefitCategory> benefitCategoryReadWriteDAO;
	@Autowired
	private ReadWriteObjectDAO<BenefitStatus> benefitStatusReadWriteDAO;
	@Autowired
	private ReadWriteObjectDAO<Brand> brandReadWriteDAO;
	@Autowired
	private ReadWriteObjectDAO<Product> productReadWriteDAO;
	@Autowired
	private ReadWriteObjectDAO<GeoArea> geoAreaReadWriteDAO;
	@Autowired
	private CompanyDAO companyDAO;

	@Test
	public void getBenefit_correct_returnEagerBenefit() {
		Language language = ModelContentCreator.createLanguage();
		languageReadWriteDAO.save(language);

		BenefitCategory category = ModelContentCreator.createBenefitCategory(language);
		benefitCategoryReadWriteDAO.save(category);

		BenefitStatus status = ModelContentCreator.createBenefitStatus(BenefitStatusCode.PUBLISHED, language);
		benefitStatusReadWriteDAO.save(status);

		Company company = ModelContentCreator.createCompany();
		companyDAO.save(company);

		GeoArea geoArea = ModelContentCreator.createGeoArea();
		geoAreaReadWriteDAO.save(geoArea);

		Brand brand = ModelContentCreator.createBrand(language);
		brandReadWriteDAO.save(brand);

		Product product = ModelContentCreator.createProduct(language);
		productReadWriteDAO.save(product);

		Benefit benefit = ModelContentCreator.createBenefit(language, category, status, geoArea, geoArea, product,
				brand, company);

		this.benefitDAO.save(benefit);

		Benefit savedBenefit = this.benefitDAO.getBenefit(benefit.getOID());
		assertNotNull(savedBenefit);
		assertEquals(status.getCode(), savedBenefit.getBenefitStatus().getCode());
		assertEquals(company.getOID(), savedBenefit.getCompany().getOID());
		assertEquals(category.getOID(), savedBenefit.getBenefitCategory().getOID());
		assertNotNull(savedBenefit.getAppliance());
		assertEquals(4, savedBenefit.getAppliance().size());
	}
}
