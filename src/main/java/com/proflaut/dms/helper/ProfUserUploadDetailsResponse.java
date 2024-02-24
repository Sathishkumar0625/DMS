package com.proflaut.dms.helper;

public class ProfUserUploadDetailsResponse {
	private String userUploadedCount;
	private String userFileOccupiedSize;
	private String noOfGroupAssigned;
	private String userGroupFileOccupiedSize;

	public String getUserGroupFileOccupiedSize() {
		return userGroupFileOccupiedSize;
	}

	public void setUserGroupFileOccupiedSize(String userGroupFileOccupiedSize) {
		this.userGroupFileOccupiedSize = userGroupFileOccupiedSize;
	}

	public String getNoOfGroupAssigned() {
		return noOfGroupAssigned;
	}

	public void setNoOfGroupAssigned(String noOfGroupAssigned) {
		this.noOfGroupAssigned = noOfGroupAssigned;
	}

	public String getUserUploadedCount() {
		return userUploadedCount;
	}

	public void setUserUploadedCount(String userUploadedCount) {
		this.userUploadedCount = userUploadedCount;
	}

	public String getUserFileOccupiedSize() {
		return userFileOccupiedSize;
	}

	public void setUserFileOccupiedSize(String userFileOccupiedSize) {
		this.userFileOccupiedSize = userFileOccupiedSize;
	}

}
