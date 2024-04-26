package com.proflaut.dms.model;

import lombok.Data;

@Data
public class GetAllRecentFolderResponse {
	private int id;
	private String ids;
	private String name;
	private String addedBy;
	private String addedOn;
}
