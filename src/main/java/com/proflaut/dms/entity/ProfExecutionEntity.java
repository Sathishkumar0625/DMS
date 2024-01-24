package com.proflaut.dms.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import javax.persistence.Index;

@Entity
@Table(name = "PROF_EXCECUTION",indexes = { @Index(columnList = "PROSPECT_ID")})
public class ProfExecutionEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private int id;

	@Column(name = "PROSPECT_ID" , unique = true)
	private String prospectId;

	@Column(name = "ACTIVITY_NAME")
	private String activityName;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "PREVIOUS_ACTIVITY")
	private String previousActivity;

	@Column(name = "ENTRY_DATE")
	private String entryDate;

	@Column(name = "ACTION_BY")
	private String actionBy;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getProspectId() {
		return prospectId;
	}

	public void setProspectId(String prospectId) {
		this.prospectId = prospectId;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPreviousActivity() {
		return previousActivity;
	}

	public void setPreviousActivity(String previousActivity) {
		this.previousActivity = previousActivity;
	}

	public String getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(String entryDate) {
		this.entryDate = entryDate;
	}

	public String getActionBy() {
		return actionBy;
	}

	public void setActionBy(String actionBy) {
		this.actionBy = actionBy;
	}

}
