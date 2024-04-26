package com.proflaut.dms.model;

import lombok.Data;

@Data
public class SearchFilesResponse {
	private int id;
	private String docName;
	private String extention;
	private String createdBy;
	private String uploadtime;
	private String fileSize;
}
