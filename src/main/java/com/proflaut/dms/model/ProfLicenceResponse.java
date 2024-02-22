package com.proflaut.dms.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ProfLicenceResponse {
	private String status;
	private String errorMessage;
	private String id;
	private String licenceKey;
	private String expiryData;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLicenceKey() {
		return licenceKey;
	}

	public void setLicenceKey(String licenceKey) {
		this.licenceKey = licenceKey;
	}

	public String getExpiryData() {
		return expiryData;
	}

	public void setExpiryData(String expiryData) {
		this.expiryData = expiryData;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
