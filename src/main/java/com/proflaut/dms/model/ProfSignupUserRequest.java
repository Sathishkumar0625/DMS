package com.proflaut.dms.model;

public class ProfSignupUserRequest {
	private String userName;
	private String webAccess;
	private String adminAccess;
	private String email;
	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getWebAccess() {
		return webAccess;
	}

	public void setWebAccess(String webAccess) {
		this.webAccess = webAccess;
	}

	public String getAdminAccess() {
		return adminAccess;
	}

	public void setAdminAccess(String adminAccess) {
		this.adminAccess = adminAccess;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
