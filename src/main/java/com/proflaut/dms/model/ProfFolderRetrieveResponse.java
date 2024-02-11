package com.proflaut.dms.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ProfFolderRetrieveResponse {
	private List<FolderPathResponse> subFolderPath;

	public List<FolderPathResponse> getSubFolderPath() {
		return subFolderPath;
	}

	public void setSubFolderPath(List<FolderPathResponse> subFolderPath) {
		this.subFolderPath = subFolderPath;
	}
}
