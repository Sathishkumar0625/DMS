package com.proflaut.dms.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "PROF_DOCUMENT_PROPERTY", indexes = { @Index(columnList = "CREATED_BY") })
public class ProfDocEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;

	@Column(name = "FOLDER_NAME")
	private String prospectId;

	@Column(name = "DOCUMENT_NAME", nullable = false)
	private String docName;

	@Column(name = "DOCUMENT_PATH")
	private String docPath;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "UPLOADED_TIME")
	private String uploadTime;

	@Column(name = "FOLDER_ID")
	private int folderId;

	@Column(name = "EXTENTION", nullable = false)
	private String extention;

	@Column(name = "IS_EMAIL")
	private String isEmail;

	@Column(name = "EMAIL_RES_ID")
	private String emilResId;

	@Column(name = "META_ID")
	private int metaId;

	@Column(name = "FILE_SIZE")
	private String fileSize;

	@Column(name = "UPLOAD_EXECUTION_TIME")
	private int uploadExecutionTime;
	
	@Column(name="STATUS")
	private String status;
}
