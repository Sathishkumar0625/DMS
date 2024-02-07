package com.proflaut.dms.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "folderName", "parentFolderID", "isParent" })
public class FolderFO {

	private String prospectId;

	private int parentFolderID;

	private String folderName;

	private String metaDataId;

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public String getMetaDataId() {
		return metaDataId;
	}

	public void setMetaDataId(String metaDataId) {
		this.metaDataId = metaDataId;
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
}
