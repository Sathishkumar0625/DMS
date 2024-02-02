package com.proflaut.dms.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(name = "PROF_DOCUMENT_PROPERTY", indexes = { @Index(columnList = "CREATED_BY") })
public class ProfDocEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;

	@Column(name = "FOLDER_NAME")
	private String prospectId;

	@Column(name = "DOCUMENT_NAME")
	private String docName;

	@Column(name = "DOCUMENT_PATH")
	private String docPath;

	@Column(name = "CREATED_BY")
	private int createdBy;

	@Column(name = "UPLOADED_TIME")
	private String uploadTime;

	@Column(name = "FOLDER_ID")
	private int folderId;

	@Column(name = "EXTENTION")
	private String extention;

	@Column(name = "IS_EMAIL")
	private String isEmail;
	
	@Column(name="EMAIL_RES_ID")
	private String emilResId;
	
	

	public String getEmilResId() {
		return emilResId;
	}

	public void setEmilResId(String emilResId) {
		this.emilResId = emilResId;
	}

	public String getIsEmail() {
		return isEmail;
	}

	public void setIsEmail(String isEmail) {
		this.isEmail = isEmail;
	}

	public String getExtention() {
		return extention;
	}

	public void setExtention(String extention) {
		this.extention = extention;
	}

	public int getFolderId() {
		return folderId;
	}

	public void setFolderId(int folderId) {
		this.folderId = folderId;
	}

	public String getProspectId() {
		return prospectId;
	}

	public void setProspectId(String prospectId) {
		this.prospectId = prospectId;
	}

	public int getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}

	public String getUploadTime() {
		return uploadTime;
	}

	public void setUploadTime(String uploadTime) {
		this.uploadTime = uploadTime;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDocName() {
		return docName;
	}

	public void setDocName(String docName) {
		this.docName = docName;
	}

	public String getDocPath() {
		return docPath;
	}

	public void setDocPath(String docPath) {
		this.docPath = docPath;
	}

}
