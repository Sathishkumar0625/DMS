package com.proflaut.dms.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PROF_LANGUAGE_CONVERTER")
public class ProfLanguageConverterEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private int id;
	@Column(name = "ORIGINAL_TEXT")
	private String originalText;
	@Column(name = "CONVERTED_TEXT")
	private String convertedText;
	@Column(name = "TEXT_ID")
	private int textId;

	public int getId() {
		return id;
	}

	public String getOriginalText() {
		return originalText;
	}

	public String getConvertedText() {
		return convertedText;
	}

	public int getTextId() {
		return textId;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setOriginalText(String originalText) {
		this.originalText = originalText;
	}

	public void setConvertedText(String convertedText) {
		this.convertedText = convertedText;
	}

	public void setTextId(int textId) {
		this.textId = textId;
	}


}
