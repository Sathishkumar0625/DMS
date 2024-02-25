package com.proflaut.dms.model;

import java.util.List;

public class ProfUserGroupDetailsResponse {
	private String groupName;
	private String groupCount;
	private List<String> groupMembers;
	private String groupUploadedFileSize;

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupCount() {
		return groupCount;
	}

	public void setGroupCount(String groupCount) {
		this.groupCount = groupCount;
	}

	public List<String> getGroupMembers() {
		return groupMembers;
	}

	public void setGroupMembers(List<String> groupMembers) {
		this.groupMembers = groupMembers;
	}

	public String getGroupUploadedFileSize() {
		return groupUploadedFileSize;
	}

	public void setGroupUploadedFileSize(String groupUploadedFileSize) {
		this.groupUploadedFileSize = groupUploadedFileSize;
	}

}
