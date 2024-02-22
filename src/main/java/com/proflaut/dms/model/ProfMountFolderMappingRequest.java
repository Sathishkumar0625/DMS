package com.proflaut.dms.model;

import java.util.List;

public class ProfMountFolderMappingRequest {
	private int mountPointId;
	private List<Integer> folderId;

	public int getMountPointId() {
		return mountPointId;
	}

	public void setMountPointId(int mountPointId) {
		this.mountPointId = mountPointId;
	}

	public List<Integer> getFolderId() {
		return folderId;
	}

	public void setFolderId(List<Integer> folderId) {
		this.folderId = folderId;
	}

}
