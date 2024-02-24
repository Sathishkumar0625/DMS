package com.proflaut.dms.model;

import java.util.List;

public class ProfOverallCountResponse {
	private String fileSizeCount;
	private String userCount;
	private String groupCount;
	private String groupFileSize;
	private String userFileSize;
	List<Groups> groups;

	public String getGroupFileSize() {
		return groupFileSize;
	}

	public void setGroupFileSize(String groupFileSize) {
		this.groupFileSize = groupFileSize;
	}

	public String getUserFileSize() {
		return userFileSize;
	}

	public void setUserFileSize(String userFileSize) {
		this.userFileSize = userFileSize;
	}

	public List<Groups> getGroups() {
		return groups;
	}

	public void setGroups(List<Groups> groups) {
		this.groups = groups;
	}

	public String getFileSizeCount() {
		return fileSizeCount;
	}

	public void setFileSizeCount(String fileSizeCount) {
		this.fileSizeCount = fileSizeCount;
	}

	public String getUserCount() {
		return userCount;
	}

	public void setUserCount(String userCount) {
		this.userCount = userCount;
	}

	public String getGroupCount() {
		return groupCount;
	}

	public void setGroupCount(String groupCount) {
		this.groupCount = groupCount;
	}

}
