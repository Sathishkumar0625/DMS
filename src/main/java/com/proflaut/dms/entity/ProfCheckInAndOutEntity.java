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
@Table(name = "PROF_CHECK_IN_AND_OUT")
public class ProfCheckInAndOutEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private int id;
	@Column(name = "FOLDER_NAME")
	private String folderName;
	@Column(name = "FOLDER_ID")
	private int folderId;
	@Column(name = "CHECK_IN")
	private String checkIn;
	@Column(name = "CHECK_OUT")
	private String checkOut;
	@Column(name = "CHECK_IN_BY")
	private String checkInBy;
	@Column(name = "CHECK_IN_TIME")
	private String checkInTime;
	@Column(name = "CHECK_OUT_TIME")
	private String checkOutTime;
	@Column(name="USER_ID")
	private int userId;
}
