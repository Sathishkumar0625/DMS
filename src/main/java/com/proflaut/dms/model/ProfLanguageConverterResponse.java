package com.proflaut.dms.model;

public class ProfLanguageConverterResponse {
	private String convertedText;
	private String status;

	public String getConvertedText() {
		return convertedText;
	}

	public String getStatus() {
		return status;
	}

	public void setConvertedText(String convertedText) {
		this.convertedText = convertedText;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
