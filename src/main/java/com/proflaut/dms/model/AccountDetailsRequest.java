package com.proflaut.dms.model;

import java.util.ArrayList;

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
	
	public ArrayList<AccountUserRequest> getAccountUserRequest() {
		return accountUserRequest;
	}

	public void setAccountUserRequest(ArrayList<AccountUserRequest> accountUserRequest) {
		this.accountUserRequest = accountUserRequest;
	}

	@JsonProperty("customerId")
	private String customerId;

	@JsonProperty("accountDetails")
	private ArrayList<AccountUserRequest> accountUserRequest;

}
