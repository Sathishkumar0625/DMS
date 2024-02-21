package com.proflaut.dms.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PTOF_MOUNTPOINT_FOLDER_MAPPING")
public class ProfMountPointFolderMappingEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private int id;
	@Column(name = "FOLDER_ID")
	private int folderId;
	@Column(name = "MOUNTPOINT_ID")
	private int mountPointId;
	@Column(name = "CREATED_AT")
	private String createdAt;
	@Column(name = "CREATED_BY")
	private String createdBy;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getFolderId() {
		return folderId;
	}

	public void setFolderId(int folderId) {
		this.folderId = folderId;
	}

	public int getMountPointId() {
		return mountPointId;
	}

	public void setMountPointId(int mountPointId) {
		this.mountPointId = mountPointId;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

}
