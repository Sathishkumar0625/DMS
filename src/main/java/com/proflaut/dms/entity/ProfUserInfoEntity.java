package com.proflaut.dms.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(name = "PROF_USERINFO", indexes = { @Index(columnList = "USER_ID"), @Index(columnList = "USER_NAME") })
public class ProfUserInfoEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "USER_ID")
	private Integer userId;

	@Column(name = "USER_NAME", unique = true)
	private String userName;

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(String updatedDate) {
		this.updatedDate = updatedDate;
	}

	@Column(name = "PASSWORD")
	private String password;

	@Column(name = "EMAIL")
	private String email;

	@Column(name = "CREATED_DATE")
	private String createdDate;

	@Column(name = "UPDATED_DATE")
	private String updatedDate;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "WEB_ACCESS")
	private String webAccess;

	@Column(name = "ADMIN_ACCESS")
	private String adminAccesss;

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

	public String getAdminAccesss() {
		return adminAccesss;
	}

	public void setAdminAccesss(String adminAccesss) {
		this.adminAccesss = adminAccesss;
	}

//	@OneToOne(cascade = CascadeType.ALL)
//	@JoinColumn(name = "PROF_USERPROPERTIES_ID", referencedColumnName = "USER_ID")
//	private ProfUserPropertiesEntity profUserPropertiesEntity;

//	public ProfUserPropertiesEntity getProfUserPropertiesEntity() {
//		return profUserPropertiesEntity;
//	}
//
//	public void setProfUserPropertiesEntity(ProfUserPropertiesEntity profUserPropertiesEntity) {
//		this.profUserPropertiesEntity = profUserPropertiesEntity;
//	}

}
