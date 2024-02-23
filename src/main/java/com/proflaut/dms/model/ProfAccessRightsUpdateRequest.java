package com.proflaut.dms.model;

import java.util.List;

public class ProfAccessRightsUpdateRequest {
	private String metaId;
	private String view;
	private String write;
	private List<ProfAccessGroupMappingRequest> group;
	private List<ProfAccessUserMappingRequest> user;

	public String getMetaId() {
		return metaId;
	}

	public void setMetaId(String metaId) {
		this.metaId = metaId;
	}

	public List<ProfAccessGroupMappingRequest> getGroup() {
		return group;
	}

	public void setGroup(List<ProfAccessGroupMappingRequest> group) {
		this.group = group;
	}

	public List<ProfAccessUserMappingRequest> getUser() {
		return user;
	}

	public void setUser(List<ProfAccessUserMappingRequest> user) {
		this.user = user;
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

}
