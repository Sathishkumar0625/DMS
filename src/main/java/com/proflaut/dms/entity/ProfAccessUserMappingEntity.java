package com.proflaut.dms.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "PROF_ACCESS_USER_MAPPING")
public class ProfAccessUserMappingEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private int id;
	@Column(name = "USER_ID")
	private String userId;
	@Column(name = "USERNAME")
	private String userName;

	@ManyToOne
	@JoinColumn(name = "access_id")
	private ProfAccessRightsEntity accessRightsEntity;

	public ProfAccessRightsEntity getAccessRightsEntity() {
		return accessRightsEntity;
	}

	public void setAccessRightsEntity(ProfAccessRightsEntity accessRightsEntity) {
		this.accessRightsEntity = accessRightsEntity;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}
