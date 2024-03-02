package com.proflaut.dms.helper;

public class ProfUserUploadDetailsResponse {
	private String userUploadedCount;
	private String userFileOccupiedSize;
	private String noOfGroupAssigned;
	private String userGroupFileOccupiedSize;
	private String userDownloadCount;
	private String userFolderCreated;
	private String averageFileUploade;
	private String averageUploadSpeed;
	private String averageDownloadSpeed;

	public String getUserDownloadCount() {
		return userDownloadCount;
	}

	public String getUserFolderCreated() {
		return userFolderCreated;
	}

	public String getAverageFileUploade() {
		return averageFileUploade;
	}

	public String getAverageUploadSpeed() {
		return averageUploadSpeed;
	}

	public String getAverageDownloadSpeed() {
		return averageDownloadSpeed;
	}

	public void setUserDownloadCount(String userDownloadCount) {
		this.userDownloadCount = userDownloadCount;
	}

	public void setUserFolderCreated(String userFolderCreated) {
		this.userFolderCreated = userFolderCreated;
	}

	public void setAverageFileUploade(String averageFileUploade) {
		this.averageFileUploade = averageFileUploade;
	}

	public void setAverageUploadSpeed(String averageUploadSpeed) {
		this.averageUploadSpeed = averageUploadSpeed;
	}

	public void setAverageDownloadSpeed(String averageDownloadSpeed) {
		this.averageDownloadSpeed = averageDownloadSpeed;
	}

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
