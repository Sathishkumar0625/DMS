package com.proflaut.dms.model;

import lombok.Data;

@Data
public class GetAllRecentFilesResponse {
	
	private int id;
	private int fileId;
	private String fileName;
	private String addedOn;
	private String addedBy;

}
