package com.proflaut.dms.model;

public class ProfGetExecutionFinalResponse {

	private String accountNumber;
	private String branchName;
	private String branchCode;
	private int customerId;
	private String ifsc;
	private String key;
	private String name;
	private String prospectId;
	
	

	public ProfGetExecutionFinalResponse() {
	}



	public ProfGetExecutionFinalResponse(String accountNumber, String branchName, String branchCode, int customerId) {
		this.accountNumber = accountNumber;
		this.branchName = branchName;
		this.branchCode = branchCode;
		this.customerId = customerId;
	}
	
	

	public ProfGetExecutionFinalResponse(String ifsc, String key, String name, String prospectId) {
		this.ifsc = ifsc;
		this.key = key;
		this.name = name;
		this.prospectId = prospectId;
	}



	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public String getIfsc() {
		return ifsc;
	}

	public void setIfsc(String ifsc) {
		this.ifsc = ifsc;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProspectId() {
		return prospectId;
	}

	public void setProspectId(String prospectId) {
		this.prospectId = prospectId;
	}

	@Override
	public String toString() {
		return "ProfGetExecutionFinalResponse [accountNumber=" + accountNumber + ", branchName=" + branchName
				+ ", branchCode=" + branchCode + ", customerId=" + customerId + ", ifsc=" + ifsc + ", key=" + key
				+ ", name=" + name + ", prospectId=" + prospectId + "]";
	}

}
