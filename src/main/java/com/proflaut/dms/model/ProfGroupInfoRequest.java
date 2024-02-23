package com.proflaut.dms.model;

import javax.validation.constraints.NotBlank;

public class ProfGroupInfoRequest {

	@NotBlank(message = "Group Name cannot be blank")
	private String groupName;

	private String createdBy;

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

}
