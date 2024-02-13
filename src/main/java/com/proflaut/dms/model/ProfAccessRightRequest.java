package com.proflaut.dms.model;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProfAccessRightRequest {
	@NotBlank(message = "Meta Id cannot be blank")
	private String metaId;
	@NotBlank(message = "View cannot be blank")
	private String view;
	@NotBlank(message = "Write cannot be blank")
	private String write;
	private String createdBy;
	
	@Valid
	@JsonProperty("group")
	private List<ProfAccessGroupMappingRequest> accessGroupMappingRequests;
	@Valid
	@JsonProperty("user")
	private List<ProfAccessUserMappingRequest> accessUserMappingRequests;

	public List<ProfAccessGroupMappingRequest> getAccessGroupMappingRequests() {
		return accessGroupMappingRequests;
	}

	public void setAccessGroupMappingRequests(List<ProfAccessGroupMappingRequest> accessGroupMappingRequests) {
		this.accessGroupMappingRequests = accessGroupMappingRequests;
	}

	public List<ProfAccessUserMappingRequest> getAccessUserMappingRequests() {
		return accessUserMappingRequests;
	}

	public void setAccessUserMappingRequests(List<ProfAccessUserMappingRequest> accessUserMappingRequests) {
		this.accessUserMappingRequests = accessUserMappingRequests;
	}

	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}

	public String getWrite() {
		return write;
	}

	public void setWrite(String write) {
		this.write = write;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getMetaId() {
		return metaId;
	}

	public void setMetaId(String metaId) {
		this.metaId = metaId;
	}

}
