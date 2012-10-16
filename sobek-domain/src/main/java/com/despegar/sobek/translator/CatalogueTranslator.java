package com.despegar.sobek.translator;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.despegar.framework.mapper.translator.AbstractIdentifiableDTOTranslator;
import com.despegar.framework.utils.string.StringUtils;
import com.despegar.sobek.dto.CatalogueDTO;
import com.despegar.sobek.exception.SobekServiceException;
import com.despegar.sobek.model.AbstractCatalogue;
import com.despegar.sobek.model.AbstractDescriptionI18N;

public class CatalogueTranslator extends
		AbstractIdentifiableDTOTranslator<CatalogueDTO, AbstractCatalogue<? extends AbstractDescriptionI18N>> {

	private static Logger logger = Logger.getLogger(CatalogueTranslator.class);

	@Override
	protected CatalogueDTO fillDTO(CatalogueDTO dto,
			AbstractCatalogue<? extends AbstractDescriptionI18N> persistentObject) {

		dto.setVersion(persistentObject.getVersion());
		dto.setOID(persistentObject.getOID());
		dto.setCode(persistentObject.getCode());

		Iterator<? extends AbstractDescriptionI18N> descriptionIterator = persistentObject.getDescriptions().iterator();
		if (!descriptionIterator.hasNext()) {
			String message = StringUtils.concat("Catalogue with code: ", persistentObject.getCode(),
					" has no description");
			logger.error(message);
			throw new SobekServiceException(message);
		}
		dto.setDescription(descriptionIterator.next().getDescription());

		return dto;
	}

	@Override
	protected AbstractCatalogue<? extends AbstractDescriptionI18N> fillPersistentObject(CatalogueDTO catalogueDTO,
			AbstractCatalogue<? extends AbstractDescriptionI18N> catalogue) {
		throw new IllegalAccessError("Catalogue can be created or updated");
	}

}
