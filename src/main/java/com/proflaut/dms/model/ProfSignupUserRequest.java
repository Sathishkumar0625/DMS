package com.proflaut.dms.model;

public class ProfSignupUserRequest {
	private String webAccess;
	private String adminAccess;
	private String email;
	private String userName;
	private String location;
	private String mobileNumber;
	private String ldap;
	private String password;
	private String status;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getLdap() {
		return ldap;
	}

	public void setLdap(String ldap) {
		this.ldap = ldap;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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
