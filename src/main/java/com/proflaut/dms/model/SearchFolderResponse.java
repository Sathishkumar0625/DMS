package com.proflaut.dms.model;

import lombok.Data;

@Data
public class SearchFolderResponse {
	private int id;
	private String docName;
	private String createdBy;
	private String uploadTime;
}
