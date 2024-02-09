package com.proflaut.dms.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ProfOverallAccessRightsResponse {
	private int id;

	private String metaId;

	private String userId;

	private String view;

	private String write;

	private String createdBy;

	private String createdAt;

	private String status;

	private String groupId;

	private String table;

	private String tablename;

	private String message;

	List<ProfAccessGroupMappingRequest> groupMappingRequests;

	List<ProfAccessUserMappingRequest> accessUserMappingRequests;

	public List<ProfAccessGroupMappingRequest> getGroupMappingRequests() {
		return groupMappingRequests;
	}

	public void setGroupMappingRequests(List<ProfAccessGroupMappingRequest> groupMappingRequests) {
		this.groupMappingRequests = groupMappingRequests;
	}

	public List<ProfAccessUserMappingRequest> getAccessUserMappingRequests() {
		return accessUserMappingRequests;
	}

	public void setAccessUserMappingRequests(List<ProfAccessUserMappingRequest> accessUserMappingRequests) {
		this.accessUserMappingRequests = accessUserMappingRequests;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getTablename() {
		return tablename;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public String getMetaId() {
		return metaId;
	}

	public void setMetaId(String metaId) {
		this.metaId = metaId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

}
