package com.proflaut.dms.model;

import java.util.List;

public class CreateTableRequest {

	private String fileExtension;
	private String tableName;
	private List<FieldDefnition> fields;

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
