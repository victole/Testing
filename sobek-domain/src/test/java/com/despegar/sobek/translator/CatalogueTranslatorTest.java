package com.despegar.sobek.translator;

import static junit.framework.Assert.assertEquals;

import java.util.Set;

import org.junit.Test;

import com.despegar.sobek.dto.CatalogueDTO;
import com.despegar.sobek.exception.SobekServiceException;
import com.despegar.sobek.model.Brand;
import com.despegar.sobek.model.BrandDescriptionI18N;
import com.google.common.collect.Sets;

public class CatalogueTranslatorTest {

	CatalogueTranslator translator = new CatalogueTranslator();

	@Test
	public void fillDTO_correct_returnsDTO() {
		BrandDescriptionI18N description = new BrandDescriptionI18N();
		description.setDescription("description");

		Brand catalogueBrand = new Brand();
		catalogueBrand.setCode("CODE");
		catalogueBrand.setOID(123L);
		catalogueBrand.setVersion(1L);
		catalogueBrand.setDescriptions(Sets.newHashSet(description));

		CatalogueDTO dto = this.translator.getDTO(catalogueBrand);
		assertEquals(catalogueBrand.getCode(), dto.getCode());
		assertEquals(catalogueBrand.getOID(), dto.getOID());
		assertEquals(catalogueBrand.getVersion(), dto.getVersion());
		assertEquals(catalogueBrand.getDescriptions().iterator().next().getDescription(), dto.getDescription());
	}

	@Test(expected = SobekServiceException.class)
	public void fillDTO_emptyDescriptions_throwSobekServiceException() {
		BrandDescriptionI18N description = new BrandDescriptionI18N();
		description.setDescription("description");

		Brand catalogueBrand = new Brand();
		catalogueBrand.setCode("CODE");
		catalogueBrand.setOID(123L);
		catalogueBrand.setVersion(1L);
		Set<BrandDescriptionI18N> emptySet = Sets.newHashSet();
		catalogueBrand.setDescriptions(emptySet);

		this.translator.getDTO(catalogueBrand);
	}

	@Test(expected = IllegalAccessError.class)
	public void fillPersistentObject_correct_throwIllegalAccessError() {
		this.translator.fillPersistentObject(new CatalogueDTO(), new Brand());
	}
}
