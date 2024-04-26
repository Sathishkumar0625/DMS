package com.proflaut.dms.model;

import lombok.Data;

@Data
public class SearchFilesResponse {
	private int id;
	private String fileName;
	private String extention;
	private String createdBy;
	private String uploadedtime;
	private String fileSize;
}
