package com.despegar.sobek.dao;

import com.despegar.framework.persistence.hibernate.dao.generic.AbstractReadObjectDAO;
import com.despegar.sobek.model.Language;

public class LanguageDAO extends AbstractReadObjectDAO<Language> {

	public Language findByIsoCode(String isoCode) {
		return this.findUnique("isoCode", isoCode);
	}
}
