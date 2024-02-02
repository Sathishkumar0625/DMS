package com.proflaut.dms.helper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proflaut.dms.entity.ProfGroupInfoEntity;
import com.proflaut.dms.entity.ProfMetaDataEntity;
import com.proflaut.dms.entity.ProfUserGroupMappingEntity;
import com.proflaut.dms.entity.ProfUserInfoEntity;
import com.proflaut.dms.model.CreateTableRequest;
import com.proflaut.dms.model.FieldDefnition;
import com.proflaut.dms.model.ProfGroupInfoRequest;
import com.proflaut.dms.model.ProfOveralUserInfoResponse;
import com.proflaut.dms.model.ProfOverallGroupInfoResponse;
import com.proflaut.dms.model.ProfUserGroupMappingRequest;
import com.proflaut.dms.repository.ProfGroupInfoRepository;
import com.proflaut.dms.repository.ProfMetaDataRepository;

@Component
public class GroupHelper {
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	ProfGroupInfoRepository groupInfoRepository;
	
	@Autowired
	ProfMetaDataRepository metaDataRepository;

	public ProfGroupInfoEntity convertGroupInfoReqToGroupInfoEnt(ProfGroupInfoRequest groupInfoRequest) {
		ProfGroupInfoEntity entity = new ProfGroupInfoEntity();
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
		ProfUserGroupMappingEntity entity = new ProfUserGroupMappingEntity();
		entity.setGroupId(mappingRequest.getGroupId());
		entity.setMappedAt(formatCurrentDateTime());
		entity.setMappedBy(mappingRequest.getMappedBy());
		entity.setStatus("A");
		entity.setUserId(mappingRequest.getUserId());
		return entity;
	}

	public ProfOveralUserInfoResponse convertToOveralUserResponse(ProfUserInfoEntity profUserInfoEntity) {
		ProfOveralUserInfoResponse response = new ProfOveralUserInfoResponse();
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
		ProfGroupInfoEntity groupInfoEnt = groupInfoRepository.findByGroupNameIgnoreCase(groupName);

		return groupInfoEnt != null;
	}

	public String createTable(List<FieldDefnition> fieldDefinitions, CreateTableRequest createTableRequest) {
		StringBuilder queryBuilder = new StringBuilder();
		int count=1;
		String tableName =createTableRequest.getTableName()+"_"+count;

		queryBuilder.append("CREATE TABLE ").append(tableName).append(" (");

		for (Iterator<FieldDefnition> it = fieldDefinitions.iterator(); it.hasNext();) {
			FieldDefnition field = it.next();
			String fieldName = field.getFieldName();
			String fieldType = field.getFieldType();
			String mandatory = field.getMandatory();
			int maxLength = Integer.parseInt(field.getMaxLength());

			queryBuilder.append(fieldName).append(" ").append(getDatabaseType(fieldType, maxLength));
			if ("Y".equalsIgnoreCase(mandatory)) {
				queryBuilder.append(" NOT NULL");
			}
			if (it.hasNext()) {
				queryBuilder.append(", ");
			}
		}
		queryBuilder.append(")");
		entityManager.createNativeQuery(queryBuilder.toString()).executeUpdate();
		count++;
		return tableName;
	}


	private String getDatabaseType(String fieldType, int maxLength) {
		if ("String".equalsIgnoreCase(fieldType)) {
			return "VARCHAR(" + maxLength + ")";
		} else if ("Integer".equalsIgnoreCase(fieldType)) {
			return "INT";
		} else {
			return fieldType;
		}
	}

	public ProfMetaDataEntity convertTableReqToMetaEntity(CreateTableRequest createTableRequest, String tableName) {
		ProfMetaDataEntity metaDataEntity = new ProfMetaDataEntity();
		
        metaDataEntity.setTableName(tableName);
        metaDataEntity.setFileExtension(createTableRequest.getFileExtension());
        metaDataEntity.setCreatedBy("Sathish");
        metaDataEntity.setCreatedAt(formatCurrentDateTime());
		return metaDataEntity;
	}
}
