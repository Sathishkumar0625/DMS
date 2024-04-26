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
@Table(name = "PROF_RECENT_FILE")
public class ProfRecentFileEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private int id;
	@Column(name = "FILE_ID")
	private int fileId;
	@Column(name = "FILE_NAME")
	private String fileName;
	@Column(name = "ADDED_ON")
	private String addedOn;
	@Column(name = "ADDED_BY")
	private String addedBy;

}
