package com.proflaut.dms.model;

import java.util.List;

public class Groups {
	private String userCount;
	private String groupName;	
	private List<GroupUserList> groupUserLists;

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getUserCount() {
		return userCount;
	}

	public void setUserCount(String userCount) {
		this.userCount = userCount;
	}

	public List<GroupUserList> getGroupUserLists() {
		return groupUserLists;
	}

	public void setGroupUserLists(List<GroupUserList> groupUserLists) {
		this.groupUserLists = groupUserLists;
	}

}
