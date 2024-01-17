package com.proflaut.dms.model;

import java.util.List;

public class FolderRetreiveResponse {
	private String status;
	
	private List<Folders> folders;
	
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<Folders> getFolder() {
		return folders;
	}
	public void setFolder(List<Folders> folder) {
		this.folders = folder;
	}
	
	
}
