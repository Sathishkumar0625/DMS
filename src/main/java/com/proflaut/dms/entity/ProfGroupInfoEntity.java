package com.proflaut.dms.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(name = "PROF_GROUP_INFO", indexes = {@Index(columnList = "ID")})
public class ProfGroupInfoEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private int id;
	@Column(name = "GROUP_NAME", unique = true, nullable = false)
	private String groupName;
	@Column(name = "STATUS")
	private String status;
	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_AT")
	private String createdAt;

	@Column(name = "USER_ID")
	private int userId;
	
	
	public ProfGroupInfoEntity() {}
	public ProfGroupInfoEntity(int id, String groupName) {
		this.id = id;
		this.groupName = groupName;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

}
