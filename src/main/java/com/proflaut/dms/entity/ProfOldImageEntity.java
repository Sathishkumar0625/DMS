package com.proflaut.dms.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PROF_OLD_IMAGE_DETAILS")
public class ProfOldImageEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "DOC_ID")
	private Integer docId;

	@Column(name = "DOCUMENT_NAME")
	private String docName;

	@Column(name = "DOCUMENT_PATH")
	private String docPath;

	@Column(name = "USER_NAME")
	private String userName;

	@Column(name = "PROSPECT_ID")
	private String prospectId;
	
	@Column(name = "CREATED_BY")
	private String createdBy;

	
	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getProspectId() {
		return prospectId;
	}

	public void setProspectId(String prospectId) {
		this.prospectId = prospectId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getDocPath() {
		return docPath;
	}

	public void setDocPath(String docPath) {
		this.docPath = docPath;
	}

	public Integer getDocId() {
		return docId;
	}

	public void setDocId(Integer docId) {
		this.docId = docId;
	}

	public String getDocName() {
		return docName;
	}

	public void setDocName(String docName) {
		this.docName = docName;
	}

}
