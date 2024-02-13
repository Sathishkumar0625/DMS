package com.proflaut.dms.model;

import javax.validation.constraints.NotBlank;

public class ProfUserGroupMappingRequest {
	@NotBlank(message = "Group Id cannot be blank")
	private String groupId;
	@NotBlank(message = "User Id cannot be blank")
	private int userId;
	private String mappedBy;

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getMappedBy() {
		return mappedBy;
	}

	public void setMappedBy(String mappedBy) {
		this.mappedBy = mappedBy;
	}

}
