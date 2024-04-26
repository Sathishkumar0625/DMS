package com.proflaut.dms.model;

import lombok.Data;

@Data
public class GetAllRecentFolderResponse {
	private int id;
	private String folderId;
	private String folderName;
	private String addedBy;
	private String addedOn;
}
