package com.despegar.sobek.utility;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.despegar.sobek.model.Appliance;
import com.despegar.sobek.model.Benefit;
import com.despegar.sobek.model.BenefitCategory;
import com.despegar.sobek.model.BenefitCategoryDescriptionI18N;
import com.despegar.sobek.model.BenefitDescriptionI18N;
import com.despegar.sobek.model.BenefitLinkRenderType;
import com.despegar.sobek.model.BenefitStatus;
import com.despegar.sobek.model.BenefitStatusCode;
import com.despegar.sobek.model.BenefitStatusDescriptionI18N;
import com.despegar.sobek.model.Brand;
import com.despegar.sobek.model.BrandDescriptionI18N;
import com.despegar.sobek.model.Company;
import com.despegar.sobek.model.GeoArea;
import com.despegar.sobek.model.Language;
import com.despegar.sobek.model.Picture;
import com.despegar.sobek.model.PictureSize;
import com.despegar.sobek.model.Product;
import com.despegar.sobek.model.ProductDescriptionI18N;
import com.despegar.sobek.model.VoucherI18N;
import com.google.common.collect.Sets;

public class ModelContentCreator {

	public static Benefit createBenefit(Language language, BenefitCategory benefitCategory,
			BenefitStatus benefitStatus, GeoArea origin, GeoArea destination, Product product, Brand brand,
			Company company) {

		Appliance brandAppliance = new Appliance();
		brandAppliance.setBrand(brand);

		Appliance productAppliance = new Appliance();
		productAppliance.setProduct(product);

		Appliance destinationAppliance = new Appliance();
		destinationAppliance.setDestinationGeoArea(destination);

		Appliance originAppliance = new Appliance();
		originAppliance.setOriginGeoArea(origin);

		Set<Appliance> appliances = Sets.newHashSet(brandAppliance, productAppliance, destinationAppliance,
				originAppliance);

		BenefitDescriptionI18N benefitDescriptionI18N = new BenefitDescriptionI18N();
		benefitDescriptionI18N.setBranches("Branch N1");
		benefitDescriptionI18N.setDescription("Description");
		benefitDescriptionI18N.setLanguage(language);
		benefitDescriptionI18N.setLink("www.link.com");
		benefitDescriptionI18N.setLinkTitle("linkTitle");
		benefitDescriptionI18N.setTermsAndConditions("terms & conditions");
		benefitDescriptionI18N.setTitle("title");
		Set<BenefitDescriptionI18N> benefitDescriptionI18Ns = Sets.newHashSet(benefitDescriptionI18N);

		VoucherI18N voucher = new VoucherI18N();
		voucher.setLanguage(language);
		voucher.setResourceName("mypdf.pdf");
		Set<VoucherI18N> voucherI18Ns = Sets.newHashSet(voucher);

		Benefit benefit = new Benefit();
		benefit.setCompany(company);
		benefit.setDateFrom(new Date(System.currentTimeMillis()));
		benefit.setDateTo(new Date(System.currentTimeMillis()));
		benefit.setIsFree(true);
		benefit.setIsOutstanding(true);
		benefit.setBenefitStatus(benefitStatus);
		benefit.setAppliance(appliances);
		benefit.setBenefitCategory(benefitCategory);
		benefit.setBenefitDescriptionI18N(benefitDescriptionI18Ns);
		benefit.setExternalResource(voucherI18Ns);
		benefit.setLinkTemplateType(BenefitLinkRenderType.NONE_RENDENRING);
		benefit.setRelevance(BigDecimal.TEN);
		return benefit;

	}

	public static Brand createBrand(Language language) {
		BrandDescriptionI18N brandDescription = new BrandDescriptionI18N();
		brandDescription.setDescription("Despegar Arg");
		brandDescription.setLanguage(language);

		Brand brand = new Brand();
		brand.setCode("DESP");
		brand.setDescriptions(Sets.newHashSet(brandDescription));
		return brand;
	}

	public static Product createProduct(Language language) {
		ProductDescriptionI18N productDescriptionI18N = new ProductDescriptionI18N();
		productDescriptionI18N.setDescription("Vuelos");
		productDescriptionI18N.setLanguage(language);

		Product product = new Product();
		product.setCode("FLIGHT");
		product.setDescriptions(Sets.newHashSet(productDescriptionI18N));
		return product;
	}

	public static GeoArea createGeoArea() {
		GeoArea geoArea = new GeoArea();
		geoArea.setType("F");
		geoArea.setDespegarItemOID(System.currentTimeMillis());
		return geoArea;
	}

	public static Benefit createBenefit() {
		Language language = new Language();
		language.setIsoCode("ES");
		language.setName("Español");

		BenefitStatus benefitStatus = createBenefitStatus(BenefitStatusCode.PUBLISHED, language);
		BenefitCategory benefitCategory = createBenefitCategory(language);

		GeoArea origin = createGeoArea();
		GeoArea destination = createGeoArea();

		Company company = createCompany();
		company.setOID(System.currentTimeMillis());

		Product product = createProduct(language);

		Brand brand = createBrand(language);

		return createBenefit(language, benefitCategory, benefitStatus, origin, destination, product, brand, company);
	}

	public static BenefitStatus createBenefitStatus(BenefitStatusCode code, Language language) {
		Set<BenefitStatusDescriptionI18N> benefitStatusDescriptionI18Ns = new HashSet<BenefitStatusDescriptionI18N>();
		BenefitStatusDescriptionI18N desc = new BenefitStatusDescriptionI18N();
		desc.setDescription("Publicado");
		desc.setLanguage(language);
		benefitStatusDescriptionI18Ns.add(desc);

		BenefitStatus benefitStatus = new BenefitStatus();
		benefitStatus.setCode(code.getCode());
		benefitStatus.setDescriptions(benefitStatusDescriptionI18Ns);
		return benefitStatus;
	}

	public static BenefitCategory createBenefitCategory(Language language) {
		BenefitCategoryDescriptionI18N benefitCategoryDescriptionI18N = new BenefitCategoryDescriptionI18N();
		benefitCategoryDescriptionI18N.setDescription("Gastronomia");
		benefitCategoryDescriptionI18N.setLanguage(language);

		Set<BenefitCategoryDescriptionI18N> categoryDescriptions = Sets.newHashSet(benefitCategoryDescriptionI18N);
		BenefitCategory benefitCategory = new BenefitCategory();
		benefitCategory.setCode("GST");
		benefitCategory.setDescriptions(categoryDescriptions);
		return benefitCategory;
	}

	public static Language createLanguage() {
		Language esLang = new Language();
		esLang.setIsoCode("EST");
		esLang.setName("Español");
		return esLang;
	}

	public static Company createCompany() {
		Company company = new Company();
		company.setAddress("Av. Corrientes 746");
		company.setCreationDate(new Date());
		company.setDescription("Oficinas Despegar");
		company.setFirm("Despegar.com");
		company.setName("Despegar");
		company.setWebsiteURL("www.despegar.com");
		company.setPicture(createPicture());
		return company;
	}
	
	public static Picture createPicture(){
		Picture picture = new Picture();
		picture.setResourceName("TST.jpg");
		return picture;
	}

	public static PictureSize createPictureSize(int width, int height) {
		PictureSize pictureSize = new PictureSize();
		pictureSize.setWidth(width);
		pictureSize.setHeight(height);
		pictureSize.setName(width + "x" + height);
		return pictureSize;
	}
}
