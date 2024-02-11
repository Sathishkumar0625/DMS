package com.proflaut.dms.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ProfFolderRetrieveResponse {
//	private int folderID;
//	private String folderName;
//	private String metaId;
//	private String isParent;
	private List<FolderPathResponse> subFolderPath;
//	private String createdBy;
//	private String createdAt;

	public List<FolderPathResponse> getSubFolderPath() {
		return subFolderPath;
	}

	public void setSubFolderPath(List<FolderPathResponse> subFolderPath) {
		this.subFolderPath = subFolderPath;
	}

//	public int getFolderID() {
//		return folderID;
//	}
//
//	public void setFolderID(int folderID) {
//		this.folderID = folderID;
//	}
//
//	public String getFolderName() {
//		return folderName;
//	}
//
//	public void setFolderName(String folderName) {
//		this.folderName = folderName;
//	}
//
//	public String getMetaId() {
//		return metaId;
//	}
//
//	public void setMetaId(String metaId) {
//		this.metaId = metaId;
//	}
//
//	public String getIsParent() {
//		return isParent;
//	}
//
//	public void setIsParent(String isParent) {
//		this.isParent = isParent;
//	}

//	public String getCreatedBy() {
//		return createdBy;
//	}
//
//	public void setCreatedBy(String createdBy) {
//		this.createdBy = createdBy;
//	}
//
//	public String getCreatedAt() {
//		return createdAt;
//	}
//
//	public void setCreatedAt(String createdAt) {
//		this.createdAt = createdAt;
//	}

}
