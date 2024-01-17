package com.proflaut.dms.model;

import java.util.Map;

import javax.persistence.Transient;

public class ProfDmsHeaderReterive {
	@Transient
	private Map<String, Object> fieldsMap;
	private String key;

	public Map<String, Object> getFieldsMap() {
		return fieldsMap;
	}

	public void setFieldsMap(Map<String, Object> fieldsMap) {
		this.fieldsMap = fieldsMap;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}
