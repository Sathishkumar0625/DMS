package com.proflaut.dms.helper;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.proflaut.dms.entity.ProfGroupInfoEntity;
import com.proflaut.dms.model.ProfGroupInfoRequest;
import com.proflaut.dms.model.ProfOverallGroupInfoResponse;

@Component
public class GroupHelper {

	public ProfGroupInfoEntity convertGroupInfoReqToGroupInfoEnt(ProfGroupInfoRequest groupInfoRequest) {
		ProfGroupInfoEntity entity=new ProfGroupInfoEntity();
		entity.setCreatedAt(LocalDateTime.now().toString());
		entity.setCreatedBy(groupInfoRequest.getCreatedBy());
		entity.setGroupName(groupInfoRequest.getGroupName());
		entity.setStatus("I");
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

}
