package com.proflaut.dms.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Index;

@Entity
@Table(name = "PROF_METADATA", indexes = { @Index(columnList = "ID") })
public class ProfMetaDataEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
//    @SequenceGenerator(name = "table_sequence", sequenceName = "table_sequence", allocationSize = 1)
	@Column(name = "ID")
	private int id;
	@Column(name = "TABLE_NAME",unique =true)
	private String tableName;
	@Column(name = "NAME",unique =true)
	private String name;
	@Column(name = "FILE_EXTENSION",nullable = false)
	private String fileExtension;
	@Column(name = "CREATED_BY")
	private String createdBy;
	@Column(name = "CREATED_AT")
	private String createdAt;
	@Column(name = "STATUS")
	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getFileExtension() {
		return fileExtension;
	}

	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

}
