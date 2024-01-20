package com.proflaut.dms.model;

public class DocumentDetails {
	private String prospectId;

	//private String image;
	private int id;
	
	private String docName;
	
	public String getUploadedBy() {
		return uploadedBy;
	}


	public void setUploadedBy(String uploadedBy) {
		this.uploadedBy = uploadedBy;
	}


	private String uploadedTime;
	
	private String uploadedBy;
	
	

	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getDocName() {
		return docName;
	}


	public void setDocName(String docName) {
		this.docName = docName;
	}


	public String getUploadedTime() {
		return uploadedTime;
	}
	
	
	public void setUploadedTime(String uploadedTime) {
		this.uploadedTime = uploadedTime;
	}


	public String getProspectId() {
		return prospectId;
	}


	public void setProspectId(String prospectId) {
		this.prospectId = prospectId;
	}

	

//	public String getImage() {
//		return image;
//	}
//
//	public void setImage(String image) {
//		this.image = image;
//	}
}
