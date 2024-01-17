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

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	private Long id;

	@Column(name = "CUSTOMER_ID")
	private String customerId;

//	@Column(name = "JOIN_ID")
//	@ManyToOne
//	@JoinColumn(name = "JOIN_ID")
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
	private int ODlimit;

	@Column(name = "BRANCH_CODE")
	private int branchCode;

	@Column(name = "IFSC_CODE")
	private String IFSCcode;

	@Column(name = "MICR_CODE")
	private String MICRcode;

	@ManyToOne
	@JoinColumn(name = "profAccountRequestEntity_id")
	private ProfAccountRequestEntity profAccountRequestEntity;

}
