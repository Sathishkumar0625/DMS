package com.proflaut.dms.model;

import java.util.List;

public class ProfAccessRightsUpdateRequest {
	private String metaId;
	private String view;
	private String write;
	private List<ProfAccessGroupMappingRequest> groups;
	private List<ProfAccessUserMappingRequest> users;

	public String getMetaId() {
		return metaId;
	}

	public void setMetaId(String metaId) {
		this.metaId = metaId;
	}

	public List<ProfAccessGroupMappingRequest> getGroups() {
		return groups;
	}

	public void setGroups(List<ProfAccessGroupMappingRequest> groups) {
		this.groups = groups;
	}

	public List<ProfAccessUserMappingRequest> getUsers() {
		return users;
	}

	public void setUsers(List<ProfAccessUserMappingRequest> users) {
		this.users = users;
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
