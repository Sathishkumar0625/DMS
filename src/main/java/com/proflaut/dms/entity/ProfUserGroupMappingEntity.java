package com.proflaut.dms.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(name = "PROF_USER_GROUPMAPPING", indexes = { @Index(columnList = "ID"), @Index(columnList = "GROUP_ID"),
		@Index(columnList = "USER_ID") })
public class ProfUserGroupMappingEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private int id;
	@Column(name = "GROUP_ID")
	private String groupId;
	@Column(name = "USER_ID")
	private int userId;
	@Column(name = "MAPPED_BY")
	private String mappedBy;
	@Column(name = "MAPPED_AT")
	private String mappedAt;
	@Column(name = "STATUS")
	private String status;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getMappedBy() {
		return mappedBy;
	}

	public void setMappedBy(String mappedBy) {
		this.mappedBy = mappedBy;
	}

	public String getMappedAt() {
		return mappedAt;
	}

	public void setMappedAt(String mappedAt) {
		this.mappedAt = mappedAt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
