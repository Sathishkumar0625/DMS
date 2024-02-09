package com.proflaut.dms.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProfAccessRightRequest {

	private String metaId;
	private String view;
	private String write;
	private String createdBy;
	@JsonProperty("group")
	private List<ProfAccessGroupMappingRequest> accessGroupMappingRequests;
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
