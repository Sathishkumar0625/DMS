package com.proflaut.dms.model;

public class ProfOveralUserInfoResponse {
	private Integer userId;

	private String userName;

	private String email;

	private String createdDate;

	private String status;

	private String webAccess;

	private String adminAccesss;

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getWebAccess() {
		return webAccess;
	}

	public void setWebAccess(String webAccess) {
		this.webAccess = webAccess;
	}

	public String getAdminAccesss() {
		return adminAccesss;
	}

	public void setAdminAccesss(String adminAccesss) {
		this.adminAccesss = adminAccesss;
	}
}