package com.proflaut.dms.model;

import lombok.Data;

@Data
public class SearchFilesResponse {
	private int id;
	private String docName;
	private String extention;
	private String createdBy;
	private String uploadTime;
	private String fileSize;
}
