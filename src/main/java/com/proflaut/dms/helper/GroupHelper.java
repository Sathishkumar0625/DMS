package com.proflaut.dms.helper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.entity.ProfGroupInfoEntity;
import com.proflaut.dms.entity.ProfGroupUserMappingEntity;
import com.proflaut.dms.entity.ProfMetaDataEntity;
import com.proflaut.dms.entity.ProfUserGroupMappingEntity;
import com.proflaut.dms.entity.ProfUserInfoEntity;
import com.proflaut.dms.entity.ProfUserPropertiesEntity;
import com.proflaut.dms.model.CreateTableRequest;
import com.proflaut.dms.model.FieldDefnition;
import com.proflaut.dms.model.ProfAssignUserRequest;
import com.proflaut.dms.model.ProfGroupInfoRequest;
import com.proflaut.dms.model.ProfOveralUserInfoResponse;
import com.proflaut.dms.model.ProfOverallGroupInfoResponse;
import com.proflaut.dms.model.ProfSignupUserRequest;
import com.proflaut.dms.model.ProfUserGroupMappingRequest;
import com.proflaut.dms.repository.ProfGroupInfoRepository;
import com.proflaut.dms.repository.ProfMetaDataRepository;

@Component
public class GroupHelper {
	
	@PersistenceContext
	private EntityManager entityManager;

	ProfGroupInfoRepository groupInfoRepository;
	ProfMetaDataRepository metaDataRepository;
	
	
	@Autowired
	public GroupHelper(ProfGroupInfoRepository groupInfoRepository, ProfMetaDataRepository metaDataRepository) {
		this.groupInfoRepository = groupInfoRepository;
		this.metaDataRepository = metaDataRepository;
	}


	public ProfGroupInfoEntity convertGroupInfoReqToGroupInfoEnt(ProfGroupInfoRequest groupInfoRequest,
			ProfUserPropertiesEntity entity2) {
		ProfGroupInfoEntity entity = new ProfGroupInfoEntity();
		entity.setCreatedAt(formatCurrentDateTime());
		entity.setCreatedBy(groupInfoRequest.getCreatedBy());
		entity.setGroupName(groupInfoRequest.getGroupName());
		entity.setStatus("A");
		entity.setUserId(entity2.getUserId());
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

	public List<ProfUserGroupMappingEntity> convertMappingInfoReqToMappingInfoEnt(
			ProfUserGroupMappingRequest mappingRequest) {
		List<ProfUserGroupMappingEntity> entities = new ArrayList<>();
		for (Integer groupId : mappingRequest.getGroupId()) {
			ProfUserGroupMappingEntity entity = new ProfUserGroupMappingEntity();
			entity.setGroupId(String.valueOf(groupId));
			entity.setUserId(mappingRequest.getUserId());
			entity.setMappedBy(mappingRequest.getMappedBy());
			entity.setMappedAt(formatCurrentDateTime());
			entity.setStatus("A");
			entities.add(entity);
		}
		return entities;
	}

	public ProfOveralUserInfoResponse convertToOveralUserResponse(ProfUserInfoEntity profUserInfoEntity) {
		ProfOveralUserInfoResponse response = new ProfOveralUserInfoResponse();
		response.setUserId(profUserInfoEntity.getUserId());
		response.setAdminAccess(profUserInfoEntity.getAdminAccesss());
		response.setCreatedDate(profUserInfoEntity.getCreatedDate());
		response.setEmail(profUserInfoEntity.getEmail());
		response.setStatus(profUserInfoEntity.getStatus());
		response.setUserName(profUserInfoEntity.getUserName());
		response.setWebAccess(profUserInfoEntity.getWebAccess());
		response.setMobileNo(profUserInfoEntity.getMobileNo());
		response.setLocation(profUserInfoEntity.getLocation());
		response.setLdap(profUserInfoEntity.getLdap());
		return response;
	}

	public boolean usernameExists(String groupName) {
		ProfGroupInfoEntity groupInfoEnt = groupInfoRepository.findByGroupNameIgnoreCase(groupName);

		return groupInfoEnt != null;
	}


	public ProfMetaDataEntity convertTableReqToMetaEntity(CreateTableRequest createTableRequest, String tableName) {
		ProfMetaDataEntity metaDataEntity = new ProfMetaDataEntity();

		metaDataEntity.setTableName(tableName);
		metaDataEntity.setFileExtension(createTableRequest.getFileExtension());
		metaDataEntity.setCreatedBy("Sathish");
		metaDataEntity.setCreatedAt(formatCurrentDateTime());
		return metaDataEntity;
	}

	public ProfGroupInfoEntity updateGroupInfoEnt(ProfGroupInfoRequest groupInfoRequest,
			ProfGroupInfoEntity groupInfo) {
		groupInfo.setStatus(groupInfoRequest.getGroupName());
		return groupInfo;
	}

	public ProfUserInfoEntity convertRequestToUser(ProfSignupUserRequest userRequest, ProfUserInfoEntity entity) {
		entity.setAdminAccesss(userRequest.getAdminAccess());
		entity.setEmail(userRequest.getEmail());
		entity.setWebAccess(userRequest.getWebAccess());
		entity.setLdap(userRequest.getLdap());
		entity.setLocation(userRequest.getLocation());
		entity.setMobileNo(userRequest.getMobileNumber());
		entity.setUserName(userRequest.getUserName());
		return entity;
	}

	public ProfOverallGroupInfoResponse convertActiveGroupInfo(ProfGroupInfoEntity profGroupInfoEntity) {
		ProfOverallGroupInfoResponse groupInfoResponse = new ProfOverallGroupInfoResponse();
		groupInfoResponse.setGroupName(profGroupInfoEntity.getGroupName());
		groupInfoResponse.setCreatedAt(profGroupInfoEntity.getCreatedAt());
		groupInfoResponse.setCreatedBy(profGroupInfoEntity.getCreatedBy());
		groupInfoResponse.setId(profGroupInfoEntity.getId());
		groupInfoResponse.setUserId(profGroupInfoEntity.getUserId());
		groupInfoResponse.setStatus(DMSConstant.SUCCESS);
		return groupInfoResponse;
	}

	public List<ProfGroupUserMappingEntity> convertAssignUserReqToProfGroupUser(
			ProfAssignUserRequest assignUserRequest) {
		List<ProfGroupUserMappingEntity> entities = new ArrayList<>();
		for (Integer userId : assignUserRequest.getUserId()) {
			ProfGroupUserMappingEntity entity = new ProfGroupUserMappingEntity();
			entity.setUserId(userId);
			entity.setGroupId(assignUserRequest.getGroupId());
			entity.setCreatedBy(assignUserRequest.getMappedBy());
			entity.setCreatedAt(formatCurrentDateTime());
			entity.setStatus("A");
			entities.add(entity);
		}
		return entities;
	}

	public ProfOveralUserInfoResponse convertEntityToResponse(List<ProfUserInfoEntity> infoEntities) {
		ProfOveralUserInfoResponse infoResponse = new ProfOveralUserInfoResponse();
		for (ProfUserInfoEntity profUserInfoEntity : infoEntities) {
			infoResponse.setUserId(profUserInfoEntity.getUserId());
			infoResponse.setUserName(profUserInfoEntity.getUserName());
			infoResponse.setCreatedDate(profUserInfoEntity.getCreatedDate());
		}
		return infoResponse;

	}

	public ProfOverallGroupInfoResponse convertGroupInfoToResponse(List<ProfGroupInfoEntity> infoEntities) {
		ProfOverallGroupInfoResponse groupInfoResponse = new ProfOverallGroupInfoResponse();
		for (ProfGroupInfoEntity profGroupInfoEntity : infoEntities) {
			groupInfoResponse.setGroupName(profGroupInfoEntity.getGroupName());
			groupInfoResponse.setCreatedAt(profGroupInfoEntity.getCreatedAt());
			groupInfoResponse.setCreatedBy(profGroupInfoEntity.getCreatedBy());
			groupInfoResponse.setUserId(profGroupInfoEntity.getUserId());
			groupInfoResponse.setId(profGroupInfoEntity.getId());
		}
		return groupInfoResponse;
	}

	public ProfOveralUserInfoResponse convertGroupUserToResponse(List<ProfUserInfoEntity> infoEntities) {
		ProfOveralUserInfoResponse infoResponses = new ProfOveralUserInfoResponse();
		for (ProfUserInfoEntity profUserInfoEntity : infoEntities) {
			infoResponses.setUserId(profUserInfoEntity.getUserId());
			infoResponses.setUserName(profUserInfoEntity.getUserName());
			infoResponses.setCreatedDate(profUserInfoEntity.getCreatedDate());
		}
		return infoResponses;
	}

	public ProfOveralUserInfoResponse convertToDashboardUserResponse(ProfUserInfoEntity infoEntity) {
		ProfOveralUserInfoResponse infoResponse = new ProfOveralUserInfoResponse();
		infoResponse.setAdminAccess(infoEntity.getAdminAccesss());
		infoResponse.setCreatedDate(infoEntity.getCreatedDate());
		infoResponse.setEmail(infoEntity.getEmail());
		infoResponse.setLdap(infoEntity.getLdap());
		infoResponse.setLocation(infoEntity.getLocation().toUpperCase());	
		infoResponse.setMobileNo(infoEntity.getMobileNo());
		infoResponse.setStatus(infoEntity.getStatus());
		infoResponse.setUserId(infoEntity.getUserId());
		infoResponse.setUserName(infoEntity.getUserName().toUpperCase());
		infoResponse.setWebAccess(infoEntity.getWebAccess());
		return infoResponse;
	}
}
