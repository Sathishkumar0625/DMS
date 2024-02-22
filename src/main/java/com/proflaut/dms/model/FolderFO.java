package com.proflaut.dms.model;

import java.util.List;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "folderName", "parentFolderID", "isParent" })
public class FolderFO {

	private String prospectId;

	private String parentFolderID;

	@NotBlank(message = "Group Id cannot be blank")
	private String folderName;

	@NotBlank(message = "Meta Data Id cannot be blank")
	private String metaDataId;

	private int mountId;

	private List<Integer> folderId;

	public int getMountId() {
		return mountId;
	}

	public void setMountId(int mountId) {
		this.mountId = mountId;
	}

	public List<Integer> getFolderId() {
		return folderId;
	}

	public void setFolderId(List<Integer> folderId) {
		this.folderId = folderId;
	}

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

	public String getParentFolderID() {
		return parentFolderID;
	}

	public void setParentFolderID(String parentFolderID) {
		this.parentFolderID = parentFolderID;
	}
}
