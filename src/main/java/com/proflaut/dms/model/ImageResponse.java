package com.proflaut.dms.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ImageResponse {

	@JsonProperty("sharpened_image")
	private String sharpenImage;

	public String getSharpenImage() {
		return sharpenImage;
	}

	public void setSharpenImage(String sharpenImage) {
		this.sharpenImage = sharpenImage;
	}

}
