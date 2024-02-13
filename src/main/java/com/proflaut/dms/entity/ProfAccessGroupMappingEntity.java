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
@Table(name = "PROF_ACCESS_GROUP_MAPPING")
public class ProfAccessGroupMappingEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private int id;
	@Column(name = "GROUP_ID",nullable = false)
	private String groupId;
	@Column(name = "GROUP_NAME",nullable = false)
	private String groupName;

	@ManyToOne()
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

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

}
