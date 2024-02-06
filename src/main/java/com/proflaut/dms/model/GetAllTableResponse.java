package com.proflaut.dms.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class GetAllTableResponse {
	private int id;
	private String createdAt;
	private String createdBy;
	private String fileExtention;
	private List<FieldDefinitionResponse> fieldNames;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getFileExtention() {
		return fileExtention;
	}

	public void setFileExtention(String fileExtention) {
		this.fileExtention = fileExtention;
	}

	public List<FieldDefinitionResponse> getFieldNames() {
		return fieldNames;
	}

	public void setFieldNames(List<FieldDefinitionResponse> fieldNames) {
		this.fieldNames = fieldNames;
	}

	

}
