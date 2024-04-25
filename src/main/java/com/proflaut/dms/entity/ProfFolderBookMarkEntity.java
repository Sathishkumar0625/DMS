package com.proflaut.dms.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "PROF_FOLDER_BOOKMARK")
public class ProfFolderBookMarkEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private int id;
	@Column(name = "FOLDER_ID")
	private int folderId;
	@Column(name = "FOLDER_NAME")
	private String folderName;
	@Column(name = "BOOKMARK_DATE_AND_TIME")
	private String bookamrkDateAndTime;
	@Column(name = "BOOKMARKED_BY")
	private String bookMarkedBy;
}
