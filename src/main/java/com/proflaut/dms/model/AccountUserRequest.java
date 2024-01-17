package com.proflaut.dms.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


@JsonPropertyOrder({ "accountNumber", "accountBranch", "address", "city", "state", "ODlimit", "branchCode", "IFSCcode",
		"MICRcode" })
public class AccountUserRequest {
	@JsonProperty("accountNumber")
	private String accountNumber;

	@JsonProperty("accountBranch")
	private String accountBranch;

	@JsonProperty("address")
	private String address;

	@JsonProperty("city")
	private String city;

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getAccountBranch() {
		return accountBranch;
	}

	public void setAccountBranch(String accountBranch) {
		this.accountBranch = accountBranch;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public int getODlimit() {
		return ODlimit;
	}

	public void setODlimit(int oDlimit) {
		ODlimit = oDlimit;
	}

	public int getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(int branchCode) {
		this.branchCode = branchCode;
	}

	public String getIFSCcode() {
		return IFSCcode;
	}

	public void setIFSCcode(String iFSCcode) {
		IFSCcode = iFSCcode;
	}

	public String getMICRcode() {
		return MICRcode;
	}

	public void setMICRcode(String mICRcode) {
		MICRcode = mICRcode;
	}

	@JsonProperty("state")
	private String state;

	@JsonProperty("ODlimit")
	private int ODlimit;

	@JsonProperty("branchCode")
	private int branchCode;

	@JsonProperty("IFSCcode")
	private String IFSCcode;

	@JsonProperty("MICRcode")
	private String MICRcode;

}
