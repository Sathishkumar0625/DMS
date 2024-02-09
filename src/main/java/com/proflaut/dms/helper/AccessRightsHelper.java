package com.proflaut.dms.helper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proflaut.dms.entity.ProfAccessRightsEntity;
import com.proflaut.dms.entity.ProfMetaDataEntity;
import com.proflaut.dms.model.ProfAccessRightRequest;
import com.proflaut.dms.model.ProfOverallAccessRightsResponse;

@Component
public class AccessRightsHelper {
	@Autowired
	private EntityManager entityManager;

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
		accessRightsEntity.setGroupId((accessRightRequest.getGroupId()));
		accessRightsEntity.setView(accessRightRequest.getView());
		accessRightsEntity.setWrite(accessRightRequest.getWrite());
		accessRightsEntity.setMetaId(accessRightRequest.getMetaId());
		accessRightsEntity.setCreatedAt(formatCurrentDateTime());
		accessRightsEntity.setStatus("A");
		accessRightsEntity.setUserId(accessRightRequest.getUserId());
		return accessRightsEntity;
	}

	public ProfOverallAccessRightsResponse convertToOverallResponse(ProfAccessRightsEntity profAccessRightsEntity, ProfMetaDataEntity dataEntity) {
		ProfOverallAccessRightsResponse accessRightsResponse = new ProfOverallAccessRightsResponse();
		accessRightsResponse.setId(profAccessRightsEntity.getId());
		accessRightsResponse.setCreatedAt(profAccessRightsEntity.getCreatedAt());
		accessRightsResponse.setCreatedBy(profAccessRightsEntity.getCreatedBy());
		accessRightsResponse.setGroupId(profAccessRightsEntity.getGroupId());
		accessRightsResponse.setMetaId(profAccessRightsEntity.getMetaId());
		accessRightsResponse.setStatus(profAccessRightsEntity.getStatus());
		accessRightsResponse.setUserId(profAccessRightsEntity.getUserId());
		accessRightsResponse.setView(profAccessRightsEntity.getView());
		accessRightsResponse.setWrite(profAccessRightsEntity.getWrite());
		accessRightsResponse.setTable(dataEntity.getTableName());
		accessRightsResponse.setTablename(dataEntity.getName());
		accessRightsResponse.setWrite(profAccessRightsEntity.getWrite());
		return accessRightsResponse;
	}

	public ProfOverallAccessRightsResponse convertAccessEntityToResponse(ProfAccessRightsEntity accessRightsEntity) {
		ProfOverallAccessRightsResponse accessRightsResponse = new ProfOverallAccessRightsResponse();
		accessRightsResponse.setCreatedAt(accessRightsEntity.getCreatedAt());
		accessRightsResponse.setCreatedBy(accessRightsEntity.getCreatedBy());
		accessRightsResponse.setGroupId(accessRightsEntity.getGroupId());
		accessRightsResponse.setId(accessRightsEntity.getId());
		accessRightsResponse.setMetaId(accessRightsEntity.getMetaId());
		accessRightsResponse.setStatus(accessRightsEntity.getStatus());
		accessRightsResponse.setUserId(accessRightsEntity.getUserId());
		accessRightsResponse.setView(accessRightsEntity.getView());
		accessRightsResponse.setWrite(accessRightsEntity.getWrite());
		return accessRightsResponse;
	}

}
