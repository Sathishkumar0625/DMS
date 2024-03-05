package com.proflaut.dms.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "DASHBOARD_DATA")
public class DashboardDataEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private int id;
	@Column(name = "DATE")
	private String date;
	@Column(name = "AVG_FILESIZE")
	private String avgFileSize;
	@Column(name = "AVG_UPLOADSPEED")
	private String avgUploadSpeed;
	@Column(name = "AVG_DOWNLOADSPEED")
	private String avgDownloadSpeed;
	@Column(name = "TOTAL_UPLOADS")
	private String totalUploads;
	@Column(name = "TOTAL_DOWNLOADS")
	private String totalDownloads;
	@Column(name = "USER_NAME")
	private String userName;
	@Column(name = "USER_ID")
	private String userId;

	public int getId() {
		return id;
	}

	public String getDate() {
		return date;
	}

	public String getUserName() {
		return userName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getAvgFileSize() {
		return avgFileSize;
	}

	public String getTotalUploads() {
		return totalUploads;
	}

	public String getTotalDownloads() {
		return totalDownloads;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setAvgFileSize(String avgFileSize) {
		this.avgFileSize = avgFileSize;
	}

	public String getAvgUploadSpeed() {
		return avgUploadSpeed;
	}

	public String getAvgDownloadSpeed() {
		return avgDownloadSpeed;
	}

	public void setAvgUploadSpeed(String avgUploadSpeed) {
		this.avgUploadSpeed = avgUploadSpeed;
	}

	public void setAvgDownloadSpeed(String avgDownloadSpeed) {
		this.avgDownloadSpeed = avgDownloadSpeed;
	}

	public void setTotalUploads(String totalUploads) {
		this.totalUploads = totalUploads;
	}

	public void setTotalDownloads(String totalDownloads) {
		this.totalDownloads = totalDownloads;
	}

}
