package com.proflaut.dms.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


@JsonPropertyOrder({ "customerId", "accountDetails" })
public class AccountDetailsRequest {

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	
	public List<AccountUserRequest> getAccountUserRequest() {
		return accountUserRequest;
	}

	public void setAccountUserRequest(List<AccountUserRequest> accountUserRequest) {
		this.accountUserRequest = accountUserRequest;
	}

	@JsonProperty("customerId")
	private String customerId;

	@JsonProperty("accountDetails")
	private List<AccountUserRequest> accountUserRequest;

}
