package com.proflaut.dms.model;

import javax.validation.constraints.NotBlank;

public class FieldDefnition {
	@NotBlank(message = "fieldName cannot be blank")
	private String fieldName;
	@NotBlank(message = "fieldType cannot be blank")
	private String fieldType;
	@NotBlank(message = "mandatory cannot be blank")
	private String mandatory;
	@NotBlank(message = "maxLength cannot be blank")
	private String maxLength;
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public String getMandatory() {
		return mandatory;
	}

	public void setMandatory(String mandatory) {
		this.mandatory = mandatory;
	}

	public String getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(String maxLength) {
		this.maxLength = maxLength;
	}
}
