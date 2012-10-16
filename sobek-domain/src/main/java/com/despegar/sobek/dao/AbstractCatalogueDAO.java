package com.despegar.sobek.dao;

import java.util.List;

import com.despegar.framework.persistence.hibernate.dao.generic.AbstractReadObjectDAO;
import com.despegar.framework.persistence.hibernate.dao.generic.query.JoinType;
import com.despegar.framework.persistence.hibernate.dao.generic.query.OrderType;
import com.despegar.framework.persistence.hibernate.dao.generic.query.QueryDefinition;
import com.despegar.sobek.model.AbstractCatalogue;
import com.despegar.sobek.model.AbstractDescriptionI18N;

public class AbstractCatalogueDAO<T extends AbstractCatalogue<? extends AbstractDescriptionI18N>> extends
		AbstractReadObjectDAO<T> {

	protected List<T> getAllCataloguesByLanguage(String languageCode) {
		QueryDefinition queryDefinition = new QueryDefinition();

		queryDefinition.addSubCriteria("descriptions", "descriptions", JoinType.LEFT);
		queryDefinition.addSubCriteria("descriptions.language", "language", JoinType.INNER);

		queryDefinition.addProperty("language.isoCode", languageCode);

		queryDefinition.addOrderBy("descriptions.description", OrderType.ASC);

		return (List<T>) this.find(queryDefinition);
	}

	public T findByCode(String code) {
		return this.findUnique("code", code);
	}
}
