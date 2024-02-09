package com.proflaut.dms.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.entity.ProfAccessRightsEntity;
import com.proflaut.dms.entity.ProfMetaDataEntity;
import com.proflaut.dms.helper.AccessRightsHelper;
import com.proflaut.dms.model.ProfAccessRightRequest;
import com.proflaut.dms.model.ProfAccessRightResponse;
import com.proflaut.dms.model.ProfOverallAccessRightsResponse;
import com.proflaut.dms.repository.ProfAccessRightRepository;
import com.proflaut.dms.repository.ProfMetaDataRepository;

@Service
public class AccessRightsServiceImpl {
	@Autowired
	private EntityManager entityManager;
	@Autowired
	ProfMetaDataRepository dataRepository;

	@Autowired
	AccessRightsHelper helper;

	@Autowired
	ProfAccessRightRepository accessRightRepository;

	@Autowired
	FileManagementServiceImpl serviceImpl;

	public Map<String, Object> findAllRowsAndColumns(String tableName) {
		Map<String, Object> responseMap = new HashMap<>();
		try {
			ProfMetaDataEntity dataEntity = dataRepository.findByNameIgnoreCase(tableName);
			if (dataEntity != null) {
				String table = dataEntity.getTableName().toLowerCase();
				List<String> columnNames = helper.getColumnNames(table);
				String columnString = String.join(",", columnNames);
				String sqlQuery = "SELECT " + columnString + " FROM " + table;
				Query query = entityManager.createNativeQuery(sqlQuery);
				@SuppressWarnings("unchecked")
				List<Object[]> resultList = query.getResultList();
				responseMap.put("fieldNames", columnNames);
				List<Map<String, Object>> valuesList = new ArrayList<>();
				for (Object[] row : resultList) {
					Map<String, Object> rowMap = new LinkedHashMap<>();
					for (int i = 0; i < columnNames.size(); i++) {
						rowMap.put(columnNames.get(i), row[i]);
					}
					valuesList.add(rowMap);
				}
				responseMap.put("values", valuesList);
			} else {
				responseMap.put("error", "Table not found");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseMap;
	}

	public ProfAccessRightResponse create(ProfAccessRightRequest accessRightRequest) {
		ProfAccessRightResponse accessRightResponse = new ProfAccessRightResponse();
		try {
			ProfAccessRightsEntity accessRights = helper.convertRequestToAccesEntity(accessRightRequest);
			accessRightRepository.save(accessRights);
			accessRightResponse.setStatus(DMSConstant.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			accessRightResponse.setStatus(DMSConstant.FAILURE);
			accessRightResponse.setErrorMessage(e.getMessage());
		}
		return accessRightResponse;
	}

	public List<ProfOverallAccessRightsResponse> findAccess() {
		List<ProfOverallAccessRightsResponse> accessRightsResponses = new ArrayList<>();
		try {
			List<ProfAccessRightsEntity> accessRightsEntity = accessRightRepository.findAll();

			for (ProfAccessRightsEntity profAccessRightsEntity : accessRightsEntity) {
				ProfMetaDataEntity dataEntity = dataRepository.findById(profAccessRightsEntity.getId());
				if (dataEntity != null) {
					ProfOverallAccessRightsResponse response = helper.convertToOverallResponse(profAccessRightsEntity,dataEntity);
					accessRightsResponses.add(response);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return accessRightsResponses;
	}

	public ProfOverallAccessRightsResponse findAccessById(String ids) {
		ProfOverallAccessRightsResponse accessRightsResponse = new ProfOverallAccessRightsResponse();
		try {
			int id = Integer.parseInt(ids);
			ProfAccessRightsEntity accessRightsEntity = accessRightRepository.findById(id);
			if (accessRightsEntity != null) {
				accessRightsResponse = helper.convertAccessEntityToResponse(accessRightsEntity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return accessRightsResponse;
	}
}
