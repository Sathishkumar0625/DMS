package com.proflaut.dms.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "folderName", "parentFolderID", "isParent" })
public class FolderFO {

	private String prospectId;

	private int parentFolderID;


	

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
