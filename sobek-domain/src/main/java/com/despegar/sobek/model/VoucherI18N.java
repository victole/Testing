package com.despegar.sobek.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "VOUCHER_I18N")
public class VoucherI18N extends AbstractResource {

	private static final long serialVersionUID = 1L;

	private Boolean isGenerated = false;
	private Language language;

	@Column(name = "GENERATED", nullable = false)
	public Boolean getIsGenerated() {
		return isGenerated;
	}

	public void setIsGenerated(Boolean isGenerated) {
		this.isGenerated = isGenerated;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "LANGUAGE_OID", nullable = false)
	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}
}