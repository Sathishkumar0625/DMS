package com.proflaut.dms.model;

import java.util.List;

public class FileRetreiveResponse {

	private String status;

	private List<DocumentDetails> document;

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
