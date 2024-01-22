package com.proflaut.dms.helper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import   java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proflaut.dms.entity.ProfGroupInfoEntity;
import com.proflaut.dms.entity.ProfUserGroupMappingEntity;
import com.proflaut.dms.entity.ProfUserInfoEntity;
import com.proflaut.dms.model.ProfGroupInfoRequest;
import com.proflaut.dms.model.ProfOveralUserInfoResponse;
import com.proflaut.dms.model.ProfOverallGroupInfoResponse;
import com.proflaut.dms.model.ProfUserGroupMappingRequest;
import com.proflaut.dms.repository.ProfGroupInfoRepository;

@Component
public class GroupHelper {
	
	@Autowired
	ProfGroupInfoRepository groupInfoRepository;
	
    public ProfGroupInfoEntity convertGroupInfoReqToGroupInfoEnt(ProfGroupInfoRequest groupInfoRequest) {
		ProfGroupInfoEntity entity=new ProfGroupInfoEntity();
		entity.setCreatedAt(formatCurrentDateTime());
		entity.setCreatedBy(groupInfoRequest.getCreatedBy());
		entity.setGroupName(groupInfoRequest.getGroupName());
		entity.setStatus("A");
		return entity;
	}
	
	public ProfOverallGroupInfoResponse convertToResponse(ProfGroupInfoEntity groupInfoEntity) {
	   
	    ProfOverallGroupInfoResponse response = new ProfOverallGroupInfoResponse();
	  
	    response.setId(groupInfoEntity.getId()); 
        response.setGroupName(groupInfoEntity.getGroupName());
        response.setStatus(groupInfoEntity.getStatus());
        response.setCreatedBy(groupInfoEntity.getCreatedBy());
        response.setCreatedAt(groupInfoEntity.getCreatedAt()); 

        return response;
	}
	 public String formatCurrentDateTime() {
	        LocalDateTime currentDateTime = LocalDateTime.now();
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(" dd-MM-yyyy HH:mm ");
	        return currentDateTime.format(formatter);
	    }

	public ProfUserGroupMappingEntity convertMappingInfoReqToMappingInfoEnt(
			ProfUserGroupMappingRequest mappingRequest) {
		ProfUserGroupMappingEntity entity=new ProfUserGroupMappingEntity();
		entity.setGroupId(mappingRequest.getGroupId());
		entity.setMappedAt(formatCurrentDateTime());
		entity.setMappedBy(mappingRequest.getMappedBy());
		entity.setStatus("A");
		entity.setUserId(mappingRequest.getUserId());
		return entity;
	}

	public ProfOveralUserInfoResponse convertToOveralUserResponse(ProfUserInfoEntity profUserInfoEntity) {
		ProfOveralUserInfoResponse response=new ProfOveralUserInfoResponse();
		response.setUserId(profUserInfoEntity.getUserId());
		response.setAdminAccesss(profUserInfoEntity.getAdminAccesss());
		response.setCreatedDate(profUserInfoEntity.getCreatedDate());
		response.setEmail(profUserInfoEntity.getEmail());
		response.setStatus(profUserInfoEntity.getStatus());
		response.setUserName(profUserInfoEntity.getUserName());
		response.setWebAccess(profUserInfoEntity.getWebAccess());
		return response;
	}

	public boolean usernameExists(String groupName) {
		ProfGroupInfoEntity groupInfoEnt=groupInfoRepository.findByGroupName(groupName);
		return groupInfoEnt != null;
	}

}
