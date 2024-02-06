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
@Table(name = "PROF_USER_CONNECTION",indexes = { @Index(columnList = "TOKEN"),
											@Index(columnList = "USER_ID")})
public class ProfUserPropertiesEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;

	@Column(name = "USER_ID", unique = true)
	private Integer userId;

	@Column(name = "LAST_USED")
	private String lastUsed;

	@Column(name = "SEC_KEY")
	private String secKey;
	
	@Column(name = "TOKEN")
	private String token;

	@Column(name = "LAST_LOGIN")
	private String lastLogin;

	@Column(name = "IS_ACTIVE")
	private String isActive;

	@Column(name = "IS_LOCKED")
	private String isLocked;

	@Column(name = "INACTIVE_DATE")
	private Timestamp inActiveDate;

	@Column(name = "LOCKED_DATE")
	private Timestamp lockedDate;
	
	@Column(name = "USER_NAME")
	private String userName;
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
	
	public String getLastUsed() {
		return lastUsed;
	}

	public void setLastUsed(String lastUsed) {
		this.lastUsed = lastUsed;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(String lastLogin) {
		this.lastLogin = lastLogin;
	}

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	public String getIsLocked() {
		return isLocked;
	}

	public void setIsLocked(String isLocked) {
		this.isLocked = isLocked;
	}

	public Timestamp getInActiveDate() {
		return inActiveDate;
	}

	public void setInActiveDate(Timestamp inActiveDate) {
		this.inActiveDate = inActiveDate;
	}

	public Timestamp getLockedDate() {
		return lockedDate;
	}

	public void setLockedDate(Timestamp lockedDate) {
		this.lockedDate = lockedDate;
	}
	public String getSecKey() {
		return secKey;
	}

	public void setSecKey(String secKey) {
		this.secKey = secKey;
	}

	

}
