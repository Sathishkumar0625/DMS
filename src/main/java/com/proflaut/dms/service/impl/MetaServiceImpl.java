package com.proflaut.dms.service.impl;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.transaction.annotation.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.entity.FolderEntity;
import com.proflaut.dms.entity.ProfAccessRightsEntity;
import com.proflaut.dms.entity.ProfAccessUserMappingEntity;
import com.proflaut.dms.entity.ProfDocEntity;
import com.proflaut.dms.entity.ProfMetaDataEntity;
import com.proflaut.dms.entity.ProfMetaDataPropertiesEntity;
import com.proflaut.dms.entity.ProfUserInfoEntity;
import com.proflaut.dms.entity.ProfUserPropertiesEntity;
import com.proflaut.dms.exception.CustomException;
import com.proflaut.dms.helper.MetaHelper;
import com.proflaut.dms.model.CreateTableRequest;
import com.proflaut.dms.model.FileRequest;
import com.proflaut.dms.model.GetAllTableResponse;
import com.proflaut.dms.model.ProfMetaDataResponse;
import com.proflaut.dms.model.ProfOverallMetaDataResponse;
import com.proflaut.dms.model.ProfUploadAccessResponse;
import com.proflaut.dms.repository.FolderRepository;
import com.proflaut.dms.repository.ProfAccessRightRepository;
import com.proflaut.dms.repository.ProfAccessUserMappingRepository;
import com.proflaut.dms.repository.ProfDocUploadRepository;
import com.proflaut.dms.repository.ProfDocumentRepository;
import com.proflaut.dms.repository.ProfMetaDataPropRepository;
import com.proflaut.dms.repository.ProfMetaDataRepository;
import com.proflaut.dms.repository.ProfUserInfoRepository;
import com.proflaut.dms.repository.ProfUserPropertiesRepository;

@Service
@Transactional
public class MetaServiceImpl {

	@Autowired
	MetaHelper metaHelper;

	@Autowired
	ProfUserPropertiesRepository profUserPropertiesRepository;
	@Autowired
	ProfUserInfoRepository profUserInfoRepository;

	@Autowired
	ProfMetaDataRepository metaDataRepository;

	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	FolderRepository folderRepository;

	@Autowired
	ProfAccessUserMappingRepository accessUserMappingRepository;

	@Autowired
	ProfAccessRightRepository accessRightRepository;

	@Autowired
	ProfMetaDataRepository profMetaDataRepository;

	@Autowired
	ProfMetaDataPropRepository dataPropRepository;

	@Autowired
	ProfDocumentRepository documentRepository;

	private static final Logger logger = LogManager.getLogger(MetaServiceImpl.class);

	@Transactional(rollbackFor = Exception.class)
	public ProfMetaDataResponse createTableFromFieldDefinitions(CreateTableRequest createTableRequest, String token) {
		ProfMetaDataResponse metaDataResponse = new ProfMetaDataResponse();
		try {
			ProfUserPropertiesEntity entity = profUserPropertiesRepository.findByToken(token);
			ProfUserInfoEntity infoEntity = profUserInfoRepository.findByUserId(entity.getUserId());

			if (entity.getToken() != null && infoEntity.getUserName() != null) {
				ProfMetaDataEntity dataEntity = metaHelper.convertTableReqToMetaEntity(createTableRequest, infoEntity);
				if (dataEntity.getName() != null && dataEntity.getFileExtension() != null) {
					entityManager.persist(dataEntity);
					String tableName = metaHelper.createTable(createTableRequest.getFields(), createTableRequest,
							dataEntity.getId());
					profMetaDataRepository.updatetableName(tableName, dataEntity.getId());
					ProfMetaDataEntity dataEnt = metaDataRepository.findById(dataEntity.getId());
					List<ProfMetaDataPropertiesEntity> propertiesList = metaHelper
							.convertMetaEntityToMetaProperties(createTableRequest, dataEnt);
					dataPropRepository.saveAll(propertiesList);

					metaDataResponse.setStatus(DMSConstant.SUCCESS);
				} else {
					throw new CustomException("ProfMetaDataEntity is Null");
				}
			} else {
				metaDataResponse.setStatus(DMSConstant.FAILURE);
				throw new CustomException("Token or userName is null");
			}
		} catch (CustomException ce) {
			ce.printStackTrace();
			metaDataResponse.setStatus(DMSConstant.FAILURE);
			metaDataResponse.setErrorMessage(ce.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			metaDataResponse.setStatus(DMSConstant.FAILURE);
			metaDataResponse.setErrorMessage("An error occurred");
		}
		return metaDataResponse;
	}

	public GetAllTableResponse getAll(ProfDocEntity docEntity) {
		GetAllTableResponse getAllTableResponse = new GetAllTableResponse();
		try {
			ProfMetaDataEntity dataEntity = metaDataRepository.findById(docEntity.getMetaId());
			if (dataEntity != null) {
				getAllTableResponse = metaHelper.convertEntityToResponse(dataEntity, docEntity.getId());
			} else {
				throw new CustomException("DataEntity not found for name: " + docEntity.getId());
			}
		} catch (CustomException ce) {
			ce.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (entityManager != null && entityManager.isOpen()) {
				entityManager.close();
			}
		}
		return getAllTableResponse;
	}

	public List<ProfOverallMetaDataResponse> getAllData() {
		List<ProfOverallMetaDataResponse> metaResponses = new ArrayList<>();
		try {
			List<ProfMetaDataEntity> dataEntities = metaDataRepository.findAll();

			for (ProfMetaDataEntity metaDataEntity : dataEntities) {
				if (!metaDataEntity.getStatus().equalsIgnoreCase("I")) {
					ProfOverallMetaDataResponse response = metaHelper.convertToResponse(metaDataEntity);
					metaResponses.add(response);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return metaResponses;
	}

	public ProfOverallMetaDataResponse getMetaData(int id) {
		ProfOverallMetaDataResponse dataResponse = new ProfOverallMetaDataResponse();
		try {
			ProfMetaDataEntity dataEntity = metaDataRepository.findById(id);
			if (dataEntity != null) {
				dataResponse = metaHelper.convertMetaEntityToResponse(dataEntity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return dataResponse;
	}

	public ProfMetaDataResponse save(CreateTableRequest createTableRequest, Integer id, FileRequest fileRequest,
			Path path, ProfDocEntity docEntity) throws IOException {
		ProfMetaDataResponse dataResponse = new ProfMetaDataResponse();
		try {
			ProfMetaDataEntity dataEntity = metaDataRepository.findByIdAndNameIgnoreCase(
					Integer.valueOf(createTableRequest.getMetadataId()), createTableRequest.getTableName());
			Optional<FolderEntity> entity = folderRepository.findById(Integer.valueOf(fileRequest.getFolderId()));
			if (dataEntity != null && !entity.isEmpty()) {
				dataResponse = metaHelper.insertDataIntoTable(dataEntity.getTableName(), createTableRequest.getFields(),
						id, path, docEntity);
			} else {
				delete(path);
				throw new CustomException("ID NOT FOUND");
			}
		} catch (Exception e) {
			if (path != null) {
				delete(path);
			}
			e.printStackTrace();
		}
		return dataResponse;
	}

	public void delete(Path path) throws IOException {
		logger.info("path ->{}", path);
		boolean isDeleted = Files.deleteIfExists(path);
		if (isDeleted) {
			logger.info("File deleted successfully");
		} else {
			logger.info("File doesn't exist");
		}
	}

	public Map<String, Object> findAllRowsAndColumns(String tableName) {
		Map<String, Object> responseMap = new HashMap<>();
		try {
			ProfMetaDataEntity dataEntity = metaDataRepository.findByNameIgnoreCase(tableName);
			if (dataEntity != null) {
				String metatableName =dataEntity.getTableName();
				List<String> columnNames = metaHelper.getColumnNames(metatableName);
				String columnString = String.join(",", columnNames);
				String sqlQuery = "SELECT " + columnString + " FROM " + metatableName;
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

	public List<ProfUploadAccessResponse> uploadAccessRights(String token) {
		List<ProfUploadAccessResponse> accessResponses = new ArrayList<>();
		try {
			ProfUserPropertiesEntity entity = profUserPropertiesRepository.findByToken(token);
			if (entity.getUserId() == null) {
				throw new CustomException("User Id Not Found");
			}
			List<ProfAccessUserMappingEntity> userAccessMappings = accessUserMappingRepository
					.findByUserId(String.valueOf(entity.getUserId()));
			List<Integer> accessIds = new ArrayList<>();

			// Retrieve access IDs from userAccessMappings
			for (ProfAccessUserMappingEntity userAccessMapping : userAccessMappings) {
				ProfAccessRightsEntity accessRightsEntity = userAccessMapping.getAccessRightsEntity();
				if (accessRightsEntity != null) {
					accessIds.add(accessRightsEntity.getId());
				}
			}
			List<ProfAccessRightsEntity> accessRights = accessRightRepository.findByIdIn(accessIds);
			for (ProfAccessRightsEntity accessRight : accessRights) {
				String metaId = accessRight.getMetaId();
				// Query metadata based on metaId
				ProfMetaDataEntity metaData = metaDataRepository.findById(Integer.parseInt(metaId));
				if (metaData != null) {
					ProfUploadAccessResponse accessResponse = new ProfUploadAccessResponse();
					accessResponse.setMetaId(Integer.valueOf(metaId));
					accessResponse.setTableName(metaData.getName());
					accessResponses.add(accessResponse);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return accessResponses;
	}

	public GetAllTableResponse getAllTable(int id) {
		GetAllTableResponse getAllTableResponse = new GetAllTableResponse();
		try {
			ProfMetaDataEntity dataEntity = metaDataRepository.findById(id);
			if (dataEntity != null) {
				getAllTableResponse = metaHelper.convertEntityToGetAllTableResponse(dataEntity);
			} else {
				throw new CustomException("DataEntity not found for name: ");
			}
		} catch (CustomException ce) {
			ce.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (entityManager != null && entityManager.isOpen()) {
				entityManager.close();
			}
		}
		return getAllTableResponse;

	}

	public Map<String, Object> search(Map<String, Object> requestBody) {
		Map<String, Object> response = new LinkedHashMap<>();
		try {
			int metaDataId = (Integer) requestBody.get("metaData_id");
			ProfMetaDataEntity metaDataEntity = metaDataRepository.findById(metaDataId);
			if (metaDataEntity != null) {
				String tableName = metaDataEntity.getTableName();
				String whereClause = buildWhereClause(requestBody, tableName);
				String selectQuery = buildSelectQuery(tableName, whereClause, metaDataId);

				System.out.println(selectQuery);
				Query query = entityManager.createNativeQuery(selectQuery);
				@SuppressWarnings("unchecked")
				List<Object[]> resultList = query.getResultList();

				List<Map<String, Object>> records = extractRecords(resultList, metaDataId);
				response.put("records", records);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	private String buildWhereClause(Map<String, Object> requestBody, String tableName) {
		StringBuilder whereClause = new StringBuilder();
		@SuppressWarnings("unchecked")
		Map<String, Object> metaData = (Map<String, Object>) requestBody.get("metaData");
		for (Map.Entry<String, Object> entry : metaData.entrySet()) {
			String originalfieldName = entry.getKey();
			String fieldName = originalfieldName.trim();
			Object value = entry.getValue();
			if (value != null) {
				if (whereClause.length() > 0) {
					whereClause.append(" AND ");
				}
				if (value instanceof String) {
					whereClause.append(tableName).append(".").append(fieldName).append(" LIKE '%").append(value)
							.append("%'");
				} else {
					whereClause.append(tableName).append(".").append(fieldName).append(" = ").append(value);
				}
			}
		}
		return whereClause.toString();
	}

	private String buildSelectQuery(String tableName, String whereClause, int metaDataId) {
		StringBuilder selectQuery = new StringBuilder("SELECT ");
		List<ProfMetaDataPropertiesEntity> dataPropertiesEntity = dataPropRepository
				.findByMetaId(String.valueOf(metaDataId));

		// Add additional columns not present in ProfMetaDataPropertiesEntity
		selectQuery.append(tableName).append(".id, ");
		selectQuery.append(tableName).append(".doc_id, ");

		// Add columns from ProfMetaDataPropertiesEntity
		for (int i = 0; i < dataPropertiesEntity.size(); i++) {
			ProfMetaDataPropertiesEntity property = dataPropertiesEntity.get(i);
			selectQuery.append(tableName).append(".").append(property.getFieldNames());
			if (i < dataPropertiesEntity.size() - 1) {
				selectQuery.append(", ");
			}
		}

		selectQuery.append(" FROM ").append(tableName);
		if (!whereClause.isEmpty()) {
			selectQuery.append(" WHERE ").append(whereClause);
		}
		return selectQuery.toString();
	}

	private List<Map<String, Object>> extractRecords(List<Object[]> resultList, int metaDataId) {
		List<Map<String, Object>> records = new ArrayList<>();
		List<ProfMetaDataPropertiesEntity> dataPropertiesEntity = dataPropRepository
				.findByMetaId(String.valueOf(metaDataId));
		int additionalColumnCount = 2;

		for (Object[] row : resultList) {
			Map<String, Object> reco = new LinkedHashMap<>();

			// Add additional columns not present in ProfMetaDataPropertiesEntity
			reco.put("id", row[0]);
			reco.put("doc_id", row[1]);
			String docId = String.valueOf(row[1]);
			List<ProfDocEntity> docEntities = getDocumentDetails(Integer.valueOf(docId));

			// Add document details to record
			if (!docEntities.isEmpty()) {
				ProfDocEntity docEntity = docEntities.get(0); // Assuming only one document for each doc_id
				reco.put("document_details", docEntity); // Add document details to record
			}
			// Add columns from ProfMetaDataPropertiesEntity
			for (int i = 0; i < dataPropertiesEntity.size(); i++) {
				ProfMetaDataPropertiesEntity property = dataPropertiesEntity.get(i);
				String columnName = property.getFieldNames();
				reco.put(columnName, row[i + additionalColumnCount]);
			}
			records.add(reco);
		}
		return records;
	}

	private List<ProfDocEntity> getDocumentDetails(int docId) {
		return documentRepository.findById(docId);
	}

}
