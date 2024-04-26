package com.proflaut.dms.model;

import lombok.Data;

@Data
public class FolderBookmark {
	private String folderId;
	private String name;
	private String size;
	private String bookmarkedBy;
	private String bookmarkDateAndTime;
	private String folderCreatedDateAndTime;

}
