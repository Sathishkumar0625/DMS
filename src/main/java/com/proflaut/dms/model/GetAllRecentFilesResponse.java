package com.proflaut.dms.model;

import lombok.Data;

@Data
public class GetAllRecentFilesResponse {
	
	private int id;
	private int ids;
	private String name;
	private String addedOn;
	private String addedBy;

}
