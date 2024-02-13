package com.proflaut.dms.model;

import javax.validation.constraints.NotBlank;

public class ProfAccessUserMappingRequest {
	@NotBlank(message = "User Name Id cannot be blank")
	private String userName;
	@NotBlank(message = "User Id cannot be blank")
	private String userId;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
