package com.proflaut.dms.model;

import javax.validation.constraints.NotBlank;

public class ProfAccessGroupMappingRequest {
	@NotBlank(message = "Group Name cannot be blank")
	private String groupName;
	@NotBlank(message = "Group Id cannot be blank")
	private String groupId;

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

}
