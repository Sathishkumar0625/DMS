package com.proflaut.dms.model;

import lombok.Data;

@Data
public class FolderBookmark {
	private String folderId;
	private String folderName;
	private String folderSize;
	private String bookmarkedBy;
	private String bookmarkDateAndTime;

}
