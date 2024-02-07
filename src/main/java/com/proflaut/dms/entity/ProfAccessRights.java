package com.proflaut.dms.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PROF_ACCESS_RIGHTS")
public class ProfAccessRights {
	@Id
	@Column(name = "ID")
	private int id;
	@Column(name = "META_ID")
	private int metaId;
	@Column(name = "USER_ID")
	private int userId;
	@Column(name = "VIEW")
	private String view;
	@Column(name = "WRITE")
	private String write;
	@Column(name = "CREATED_BY")
	private String createdBy;
	@Column(name = "CREATED_AT")
	private String createdAt;
	@Column(name = "STATUS")
	private String status;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMetaId() {
		return metaId;
	}

	public void setMetaId(int metaId) {
		this.metaId = metaId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}

	public String getWrite() {
		return write;
	}

	public void setWrite(String write) {
		this.write = write;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
