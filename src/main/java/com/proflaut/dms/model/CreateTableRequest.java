package com.proflaut.dms.model;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class CreateTableRequest {

	@NotBlank(message = "File extension cannot be blank")
	private String fileExtension;
	private String metadataId;
	@NotBlank(message = "Table Name cannot be blank")
	private String tableName;
	@NotNull
	@Valid
	private List<FieldDefnition> fields;

	public String getMetadataId() {
		return metadataId;
	}

	public void setMetadataId(String metadataId) {
		this.metadataId = metadataId;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getFileExtension() {
		return fileExtension;
	}

	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	public List<FieldDefnition> getFields() {
		return fields;
	}

	public void setFields(List<FieldDefnition> fields) {
		this.fields = fields;
	}

}
