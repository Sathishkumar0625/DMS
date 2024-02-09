package com.proflaut.dms.helper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proflaut.dms.entity.ProfAccessGroupMappingEntity;
import com.proflaut.dms.entity.ProfAccessRightsEntity;
import com.proflaut.dms.entity.ProfAccessUserMappingEntity;
import com.proflaut.dms.entity.ProfMetaDataEntity;
import com.proflaut.dms.model.ProfAccessGroupMappingRequest;
import com.proflaut.dms.model.ProfAccessRightRequest;
import com.proflaut.dms.model.ProfAccessUserMappingRequest;
import com.proflaut.dms.model.ProfOverallAccessRightsResponse;
import com.proflaut.dms.repository.ProfAccessGroupMappingRepository;
import com.proflaut.dms.repository.ProfAccessUserMappingRepository;

@Component
public class AccessRightsHelper {
	@Autowired
	private EntityManager entityManager;

	@Autowired
	ProfAccessGroupMappingRepository groupMappingRepository;

	@Autowired
	ProfAccessUserMappingRepository userMappingRepository;

	public static String formatCurrentDateTime() {
		LocalDateTime currentDateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(" dd-MM-yyyy HH:mm ");
		return currentDateTime.format(formatter);
	}

	@SuppressWarnings("unchecked")
	public List<String> getColumnNames(String tableName) {
		List<String> columnNames = new ArrayList<>();
		try {
			String sqlQuery = "SELECT column_name FROM information_schema.columns WHERE table_name = :tableName";
			Query query = entityManager.createNativeQuery(sqlQuery);
			query.setParameter("tableName", tableName);
			columnNames = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return columnNames;
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
		List<ProfAccessGroupMappingEntity> groupMappingEntities = groupMappingRepository.findById(id);
		List<ProfAccessUserMappingEntity> accessUserMappingEntities = userMappingRepository.findById(id);
		if (!groupMappingEntities.isEmpty() && accessUserMappingEntities.isEmpty()) {
			accessRightsResponse.setCreatedAt(accessRightsEntity.getCreatedAt());
			accessRightsResponse.setCreatedBy(accessRightsEntity.getCreatedBy());
			accessRightsResponse.setId(accessRightsEntity.getId());
			accessRightsResponse.setMetaId(accessRightsEntity.getMetaId());
			accessRightsResponse.setStatus(accessRightsEntity.getStatus());
			accessRightsResponse.setView(accessRightsEntity.getView());
			accessRightsResponse.setWrite(accessRightsEntity.getWrite());
			accessRightsResponse.setTable(dataEntity.getTableName());
			accessRightsResponse.setTablename(dataEntity.getName());
			List<ProfAccessGroupMappingRequest> accessRightRequest = new ArrayList<>();
			for (ProfAccessGroupMappingEntity groupMappingRequest : groupMappingEntities) {
				ProfAccessGroupMappingRequest groupMappingRequests = new ProfAccessGroupMappingRequest();
				groupMappingRequests.setGroupId(groupMappingRequest.getGroupId());
				groupMappingRequests.setGroupName(groupMappingRequest.getGroupName());
				accessRightRequest.add(groupMappingRequests);
			}
			accessRightsResponse.setGroupMappingRequests(accessRightRequest);
			List<ProfAccessUserMappingRequest> accessUserMappingRequests = new ArrayList<>();
			for (ProfAccessUserMappingEntity userMappingRequest : accessUserMappingEntities) {
				ProfAccessUserMappingRequest mappingRequest = new ProfAccessUserMappingRequest();
				mappingRequest.setUserId(userMappingRequest.getUserId());
				mappingRequest.setUserName(userMappingRequest.getUserName());
				accessUserMappingRequests.add(mappingRequest);
			}
			accessRightsResponse.setAccessUserMappingRequests(accessUserMappingRequests);
		}
		return accessRightsResponse;
	}

}
