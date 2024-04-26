package com.proflaut.dms.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "PROF_FOLDER", indexes = { @Index(columnList = "ID") })
public class FolderEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	private int id;

	@Column(name = "PROSPECT_ID")
	private String prospectId;

	@Column(name = "PARENT_FOLDER_ID")
	private int parentFolderID;

	@Column(name = "IS_PARENT")
	private String isParent;

	@Lob
	@Column(name = "FOLDER_PATH", columnDefinition = "CLOB")
	private String folderPath;

	@Column(name = "FOLDER_NAME")
	private String folderName;

	@Column(name = "META_ID")
	private String metaId;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_AT")
	private String createdAt;

	@Column(name = "STATUS")
	private String status;

}
