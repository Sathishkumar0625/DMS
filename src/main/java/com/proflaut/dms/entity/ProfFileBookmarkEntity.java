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
@Table(name = "PROF_FILE_BOOKMARK")
public class ProfFileBookmarkEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private int id;
	@Column(name = "FILE_NAME")
	private String fileName;
	@Column(name = "FILE_ID")
	private int fileId;
	@Column(name = "BOOKMARK_DATE_AND_TIME")
	private String bookmarkDateAndTime;
	@Column(name = "BOOKMARKED_BY")
	private String bookmarkedBy;
}
