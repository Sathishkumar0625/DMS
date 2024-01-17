package com.proflaut.dms.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

	
@Entity
@Table(name = "PROF_USERINFO",indexes = { @Index(columnList = "USER_ID"),
										@Index(columnList = "USER_NAME") })
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

	public Timestamp getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public Timestamp getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Timestamp updatedDate) {
		this.updatedDate = updatedDate;
	}

	@Column(name = "PASSWORD")
	private String password;

	@Column(name = "EMAIL")
	private String email;

	@Column(name = "CREATED_DATE")
	private Timestamp createdDate;

	@Column(name = "UPDATED_DATE")
	private Timestamp updatedDate;

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
