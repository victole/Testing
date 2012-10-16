package com.despegar.sobek.model;

public enum BenefitStatusCode {

	PUBLISHED("PUB"), UNPUBLISHED("UNP"), CANCELLED("CAN");

	private String code;

	private BenefitStatusCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
