package com.proflaut.dms.model;

import javax.validation.constraints.NotBlank;

public class ProfGroupInfoRequest {

	private String createdBy;
	@NotBlank(message = "Group Name cannot be blank")
	private String groupName;
	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

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
