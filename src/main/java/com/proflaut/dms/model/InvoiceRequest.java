package com.proflaut.dms.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InvoiceRequest {

	private String applicantName;

	private String officeAddress;

	private String addressOfFactory;

	private String community;

	private String mobileNo;

	private String eMailAddress;

	private String panCardNo;

	private String dob;

	private String state;

	private String district;

	@JsonProperty("proprietorParDirRequests")
	private List<ProprietorParDirRequest> proprietorParDirRequests;

	@JsonProperty("associateConcerns")
	private List<AssociateConcerns> associateConcerns;

	@JsonProperty("bankingCreditFacilities")
	private List<BankingCreditFacilities> bankingCreditFacilities;

	@JsonProperty("creditFacilities")
	private List<CreditFacilities> creditFacilities;

	public List<CreditFacilities> getCreditFacilities() {
		return creditFacilities;
	}

	public void setCreditFacilities(List<CreditFacilities> creditFacilities) {
		this.creditFacilities = creditFacilities;
	}

	public List<BankingCreditFacilities> getBankingCreditFacilities() {
		return bankingCreditFacilities;
	}

	public void setBankingCreditFacilities(List<BankingCreditFacilities> bankingCreditFacilities) {
		this.bankingCreditFacilities = bankingCreditFacilities;
	}

	public List<AssociateConcerns> getAssociateConcerns() {
		return associateConcerns;
	}

	public void setAssociateConcerns(List<AssociateConcerns> associateConcerns) {
		this.associateConcerns = associateConcerns;
	}

	public String getApplicantName() {
		return applicantName;
	}

	public void setApplicantName(String applicantName) {
		this.applicantName = applicantName;
	}

	public String getOfficeAddress() {
		return officeAddress;
	}

	public void setOfficeAddress(String officeAddress) {
		this.officeAddress = officeAddress;
	}

	public String getAddressOfFactory() {
		return addressOfFactory;
	}

	public void setAddressOfFactory(String addressOfFactory) {
		this.addressOfFactory = addressOfFactory;
	}

	public String getCommunity() {
		return community;
	}

	public void setCommunity(String community) {
		this.community = community;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String geteMailAddress() {
		return eMailAddress;
	}

	public void seteMailAddress(String eMailAddress) {
		this.eMailAddress = eMailAddress;
	}

	public String getPanCardNo() {
		return panCardNo;
	}

	public void setPanCardNo(String panCardNo) {
		this.panCardNo = panCardNo;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public List<ProprietorParDirRequest> getProprietorParDirRequests() {
		return proprietorParDirRequests;
	}

	public void setProprietorParDirRequests(List<ProprietorParDirRequest> proprietorParDirRequests) {
		this.proprietorParDirRequests = proprietorParDirRequests;
	}

}
