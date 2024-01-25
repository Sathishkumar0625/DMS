package com.proflaut.dms.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({ "docName", "image" })
public class FileRequest {
	
	@NotBlank(message = "Document name cannot be blank")
	@JsonProperty("docName")
	private String dockName;
	
	@NotBlank(message = "ProspectId cannot be blank")
	@JsonProperty("prospectId")
	private String prospectId;
	
	@NotBlank(message = "image cannot be blank")
	@JsonProperty("image")
	private String image;
	
	private String extention;
	
	
	
	public String getExtention() {
		return extention;
	}

	public void setExtention(String extention) {
		this.extention = extention;
	}

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

	@JsonProperty("docPath")
	private String dockPath;

	public String getDockPath() {
		return dockPath;
	}
	
	public void setDockPath(String dockPath) {
		this.dockPath = dockPath;
	}

	public String getProspectId() {
		return prospectId;
	}

	public void setProspectId(String prospectId) {
		this.prospectId = prospectId;
	}

	
	
}
