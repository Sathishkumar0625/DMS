package com.proflaut.dms.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "PROF_ACCESS_RIGHTS")
public class ProfAccessRightsEntity {
	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(name = "META_ID")
	private String metaId;
	@Column(name = "VIEW")
	private String view;
	@Column(name = "WRITE")
	private String write;
	@Column(name = "CREATED_BY")
	private String createdBy;
	@Column(name = "CREATED_AT")
	private String createdAt;
	@Column(name = "STATUS")
	private String status;

	@OneToMany(mappedBy = "accessRightsEntity", cascade = CascadeType.ALL)
	private List<ProfAccessGroupMappingEntity> accessGroupMappingEntities;

	@OneToMany(mappedBy = "accessRightsEntity", cascade = CascadeType.ALL)
	private List<ProfAccessUserMappingEntity> accessUserMappingEntities;

	public List<ProfAccessGroupMappingEntity> getAccessGroupMappingEntities() {
		return accessGroupMappingEntities;
	}

	public void setAccessGroupMappingEntities(List<ProfAccessGroupMappingEntity> accessGroupMappingEntities) {
		this.accessGroupMappingEntities = accessGroupMappingEntities;
	}

	public List<ProfAccessUserMappingEntity> getAccessUserMappingEntities() {
		return accessUserMappingEntities;
	}

	public void setAccessUserMappingEntities(List<ProfAccessUserMappingEntity> accessUserMappingEntities) {
		this.accessUserMappingEntities = accessUserMappingEntities;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMetaId() {
		return metaId;
	}

	public void setMetaId(String metaId) {
		this.metaId = metaId;
	}

	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}

	public String getWrite() {
		return write;
	}

	public void setWrite(String write) {
		this.write = write;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
