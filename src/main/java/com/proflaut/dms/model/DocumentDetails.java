package com.proflaut.dms.model;

public class DocumentDetails {
	private int docId;

	//private String image;
	
	private String docName;
	
	private String uploadedTime;
	
	

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

	public int getDocId() {
		return docId;
	}

	public void setDocId(int docId) {
		this.docId = docId;
	}

//	public String getImage() {
//		return image;
//	}
//
//	public void setImage(String image) {
//		this.image = image;
//	}
}
