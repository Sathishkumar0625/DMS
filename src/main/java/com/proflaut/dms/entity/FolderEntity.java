package com.proflaut.dms.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PROF_FOLDER")
public class FolderEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	private Integer id;

	@Column(name = "PROSPECT_ID" , unique = true)
	private String prospectId;

	@Column(name = "PARENT_FOLDER_ID")
	private int parentFolderID;

	@Column(name = "IS_PARENT")
	private String isParent;

	@Column(name = "FOLDER_PATH")
	private String folderPath;

	@Column(name = "TOKEN")
	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getFolderPath() {
		return folderPath;
	}

	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	

	public String getProspectId() {
		return prospectId;
	}

	public void setProspectId(String prospectId) {
		this.prospectId = prospectId;
	}

	public int getParentFolderID() {
		return parentFolderID;
	}

	public void setParentFolderID(int parentFolderID) {
		this.parentFolderID = parentFolderID;
	}

	public String getIsParent() {
		return isParent;
	}

	public void setIsParent(String isParent) {
		this.isParent = isParent;
	}

}
