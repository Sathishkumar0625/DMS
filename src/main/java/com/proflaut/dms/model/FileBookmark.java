package com.proflaut.dms.model;

import lombok.Data;

@Data
public class FileBookmark {

	private String name;
	private String fileId;
	private String size;
	private String bookmarkedBy;
	private String bookmarkDateAndTime;
	private String fileUploadeddateAndTime;
}
