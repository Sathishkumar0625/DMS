package com.proflaut.dms.model;


import lombok.Data;

@Data
public class DocumentDetails {
	private String prospectId;

	private int id;
	
	private String docName;

	private String uploadedTime;
	
	private String uploadedBy;

}
