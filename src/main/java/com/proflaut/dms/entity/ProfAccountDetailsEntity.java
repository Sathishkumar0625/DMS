package com.proflaut.dms.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "PROF_CUSTOMERACCOUNTINFO")
public class ProfAccountDetailsEntity {

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCustomerId() {
		return customerId;
	}

	public int getOdLimit() {
		return odLimit;
	}

	public String getIfscCode() {
		return ifscCode;
	}

	public String getMicrCode() {
		return micrCode;
	}

	public void setOdLimit(int odLimit) {
		this.odLimit = odLimit;
	}

	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}

	public void setMicrCode(String micrCode) {
		this.micrCode = micrCode;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

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

	public int getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(int branchCode) {
		this.branchCode = branchCode;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	private Long id;

	@Column(name = "CUSTOMER_ID")
	private String customerId;

	private Integer joinID;

	public Integer getJoinID() {
		return joinID;
	}

	public void setJoinID(Integer joinID) {
		this.joinID = joinID;
	}

	public ProfAccountRequestEntity getProfAccountRequestEntity() {
		return profAccountRequestEntity;
	}

	public void setProfAccountRequestEntity(ProfAccountRequestEntity profAccountRequestEntity) {
		this.profAccountRequestEntity = profAccountRequestEntity;
	}

	@Column(name = "ACCOUNT_NUMBER")
	private String accountNumber;

	@Column(name = "ACCOUNT_BRANCH")
	private String accountBranch;

	@Column(name = "ADDRESS")
	private String address;

	@Column(name = "CITY")
	private String city;

	@Column(name = "STATE")
	private String state;

	@Column(name = "OD_LIMIT")
	private int odLimit;

	@Column(name = "BRANCH_CODE")
	private int branchCode;

	@Column(name = "IFSC_CODE")
	private String ifscCode;

	@Column(name = "MICR_CODE")
	private String micrCode;

	@ManyToOne
	@JoinColumn(name = "profAccountRequestEntity_id")
	private ProfAccountRequestEntity profAccountRequestEntity;

}
