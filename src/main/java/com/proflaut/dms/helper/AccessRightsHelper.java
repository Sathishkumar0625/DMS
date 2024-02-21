package com.proflaut.dms.helper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proflaut.dms.entity.ProfAccessGroupMappingEntity;
import com.proflaut.dms.entity.ProfAccessRightsEntity;
import com.proflaut.dms.entity.ProfAccessUserMappingEntity;
import com.proflaut.dms.entity.ProfMetaDataEntity;
import com.proflaut.dms.model.ProfAccessGroupMappingRequest;
import com.proflaut.dms.model.ProfAccessRightRequest;
import com.proflaut.dms.model.ProfAccessRightsUpdateRequest;
import com.proflaut.dms.model.ProfAccessUserMappingRequest;
import com.proflaut.dms.model.ProfOverallAccessRightsResponse;
import com.proflaut.dms.repository.ProfAccessGroupMappingRepository;
import com.proflaut.dms.repository.ProfAccessUserMappingRepository;

@Component
public class AccessRightsHelper {

	@Autowired
	ProfAccessGroupMappingRepository groupMappingRepository;

	@Autowired
	ProfAccessUserMappingRepository userMappingRepository;

	public static String formatCurrentDateTime() {
		LocalDateTime currentDateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(" dd-MM-yyyy HH:mm ");
		return currentDateTime.format(formatter);
	}

	public ProfAccessRightsEntity convertRequestToAccesEntity(ProfAccessRightRequest accessRightRequest) {
		ProfAccessRightsEntity accessRightsEntity = new ProfAccessRightsEntity();
		accessRightsEntity.setCreatedBy(accessRightRequest.getCreatedBy());
		accessRightsEntity.setView(accessRightRequest.getView());
		accessRightsEntity.setWrite(accessRightRequest.getWrite());
		accessRightsEntity.setMetaId(accessRightRequest.getMetaId());
		accessRightsEntity.setCreatedAt(formatCurrentDateTime());
		accessRightsEntity.setStatus("A");
		List<ProfAccessGroupMappingEntity> groupMappingEntities = new ArrayList<>();
		for (ProfAccessGroupMappingRequest groupMappingRequest : accessRightRequest.getAccessGroupMappingRequests()) {
			ProfAccessGroupMappingEntity groupMappingEntity = new ProfAccessGroupMappingEntity();
			groupMappingEntity.setGroupId(groupMappingRequest.getGroupId());
			groupMappingEntity.setGroupName(groupMappingRequest.getGroupName());
			groupMappingEntity.setAccessRightsEntity(accessRightsEntity);
			groupMappingEntities.add(groupMappingEntity);
		}
		accessRightsEntity.setAccessGroupMappingEntities(groupMappingEntities);
		List<ProfAccessUserMappingEntity> userMappingEntities = new ArrayList<>();
		for (ProfAccessUserMappingRequest userMappingRequest : accessRightRequest.getAccessUserMappingRequests()) {
			ProfAccessUserMappingEntity userMappingEntity = new ProfAccessUserMappingEntity();
			userMappingEntity.setUserId(userMappingRequest.getUserId());
			userMappingEntity.setUserName(userMappingRequest.getUserName());
			userMappingEntity.setAccessRightsEntity(accessRightsEntity);
			userMappingEntities.add(userMappingEntity);
		}
		accessRightsEntity.setAccessUserMappingEntities(userMappingEntities);
		return accessRightsEntity;
	}

	public ProfOverallAccessRightsResponse convertToOverallResponse(ProfAccessRightsEntity profAccessRightsEntity,
			ProfMetaDataEntity dataEntity) {
		ProfOverallAccessRightsResponse accessRightsResponse = new ProfOverallAccessRightsResponse();
		accessRightsResponse.setId(profAccessRightsEntity.getId());
		accessRightsResponse.setCreatedAt(profAccessRightsEntity.getCreatedAt());
		accessRightsResponse.setCreatedBy(profAccessRightsEntity.getCreatedBy());
		accessRightsResponse.setMetaId(profAccessRightsEntity.getMetaId());
		accessRightsResponse.setStatus(profAccessRightsEntity.getStatus());
		accessRightsResponse.setView(profAccessRightsEntity.getView());
		accessRightsResponse.setWrite(profAccessRightsEntity.getWrite());
		accessRightsResponse.setTable(dataEntity.getTableName());
		accessRightsResponse.setTablename(dataEntity.getName());
		accessRightsResponse.setWrite(profAccessRightsEntity.getWrite());
		return accessRightsResponse;
	}

	public ProfOverallAccessRightsResponse convertAccessEntityToResponse(ProfAccessRightsEntity accessRightsEntity,
			ProfMetaDataEntity dataEntity, int id) {
		ProfOverallAccessRightsResponse accessRightsResponse = new ProfOverallAccessRightsResponse();
		accessRightsResponse.setId(accessRightsEntity.getId());
		accessRightsResponse.setMetaId(accessRightsEntity.getMetaId());
		accessRightsResponse.setView(accessRightsEntity.getView());
		accessRightsResponse.setWrite(accessRightsEntity.getWrite());
		accessRightsResponse.setCreatedBy(accessRightsEntity.getCreatedBy());
		accessRightsResponse.setCreatedAt(accessRightsEntity.getCreatedAt());
		accessRightsResponse.setStatus(accessRightsEntity.getStatus());
		accessRightsResponse.setTable(dataEntity.getTableName());
		accessRightsResponse.setTablename(dataEntity.getName());

		List<ProfAccessGroupMappingEntity> groupMappingEntities = accessRightsEntity.getAccessGroupMappingEntities();
		List<ProfAccessUserMappingEntity> accessUserMappingEntities = accessRightsEntity.getAccessUserMappingEntities();

		List<ProfAccessGroupMappingRequest> groupMappingRequests = new ArrayList<>();
		for (ProfAccessGroupMappingEntity groupMappingEntity : groupMappingEntities) {
			ProfAccessGroupMappingRequest groupMappingRequest = new ProfAccessGroupMappingRequest();
			groupMappingRequest.setGroupId(groupMappingEntity.getGroupId());
			groupMappingRequest.setGroupName(groupMappingEntity.getGroupName());
			groupMappingRequests.add(groupMappingRequest);
		}
		accessRightsResponse.setGroupMappingRequests(groupMappingRequests);

		List<ProfAccessUserMappingRequest> userMappingRequests = new ArrayList<>();
		for (ProfAccessUserMappingEntity userMappingEntity : accessUserMappingEntities) {
			ProfAccessUserMappingRequest userMappingRequest = new ProfAccessUserMappingRequest();
			userMappingRequest.setUserId(userMappingEntity.getUserId());
			userMappingRequest.setUserName(userMappingEntity.getUserName());
			userMappingRequests.add(userMappingRequest);
		}
		accessRightsResponse.setAccessUserMappingRequests(userMappingRequests);

		return accessRightsResponse;
	}

	public ProfAccessRightsEntity convertRequestToUpdateAcess(ProfAccessRightsEntity accessRightsEntity,
			ProfAccessRightsUpdateRequest accessRightsUpdateRequest) {
		accessRightsEntity.setView(accessRightsUpdateRequest.getView());
		accessRightsEntity.setWrite(accessRightsUpdateRequest.getWrite());
		return accessRightsEntity;
		
	}

}
