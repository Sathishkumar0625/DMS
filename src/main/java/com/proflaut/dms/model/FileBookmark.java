package com.proflaut.dms.model;

import lombok.Data;

@Data
public class FileBookmark {

	private String fileName;
	private String fileId;
	private String fileSize;
	private String bookmarkedBy;
	private String bookmarkDateAndTime;
	private String fileUploadeddateAndTime;
}
