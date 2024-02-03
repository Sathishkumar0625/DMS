package com.proflaut.dms.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class FileRetreiveResponse {

	private String status;
	
	private String errorMessage;

	private List<DocumentDetails> document;

	
	
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<DocumentDetails> getDocument() {
		return document;
	}

	public void setDocument(List<DocumentDetails> document) {
		this.document = document;
	}

}
