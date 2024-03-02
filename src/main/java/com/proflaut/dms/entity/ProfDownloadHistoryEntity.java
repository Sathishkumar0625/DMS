package com.proflaut.dms.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PROF_DOWNLOAD_HISTORY")
public class ProfDownloadHistoryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private int id;

	@Column(name = "USER_ID")
	private int userId;

	@Column(name = "DOC_ID")
	private int docId;

	@Column(name = "DOWNLOAD_DATE")
	private String downloadedDate;

	@Column(name = "DOWNLOAD_EXECUTIONSPEED")
	private int downloadExecutionSpeed;

	public int getDownloadExecutionSpeed() {
		return downloadExecutionSpeed;
	}

	public void setDownloadExecutionSpeed(int downloadExecutionSpeed) {
		this.downloadExecutionSpeed = downloadExecutionSpeed;
	}

	public int getId() {
		return id;
	}

	public int getUserId() {
		return userId;
	}

	public int getDocId() {
		return docId;
	}

	public String getDownloadedDate() {
		return downloadedDate;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void setDocId(int docId) {
		this.docId = docId;
	}

	public void setDownloadedDate(String downloadedDate) {
		this.downloadedDate = downloadedDate;
	}

}
