package com.proflaut.dms.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({ "docName", "image" })
public class FileRequest {
	
	@JsonProperty("docName")
	private String dockName;
	
	@JsonProperty("docId")
	private int docId;
	
	public String getDockName() {
		return dockName;
	}
	
	public void setDockName(String dockName) {
		this.dockName = dockName;
	}
	
	public String getImage() {
		return image;
	}
	
	public void setImage(String image) {
		this.image = image;
	}

	@JsonProperty("image")
	private String image;

	@JsonProperty("docPath")
	private String dockPath;

	public String getDockPath() {
		return dockPath;
	}
	
	public void setDockPath(String dockPath) {
		this.dockPath = dockPath;
	}

	public int getDocId() {
		return docId;
	}

	public void setDocId(int docId) {
		this.docId = docId;
	}
	
}
