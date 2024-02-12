package com.proflaut.dms.helper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.stereotype.Component;

import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.entity.ProfMetaDataEntity;
import com.proflaut.dms.entity.ProfUserInfoEntity;
import com.proflaut.dms.model.CreateTableRequest;
import com.proflaut.dms.model.FieldDefinitionResponse;
import com.proflaut.dms.model.FieldDefnition;
import com.proflaut.dms.model.GetAllTableResponse;
import com.proflaut.dms.model.ProfMetaDataResponse;
import com.proflaut.dms.model.ProfOverallMetaDataResponse;

@Component
public class MetaHelper {

	@PersistenceContext
	EntityManager entityManager;

	public String formatCurrentDateTime() {
		LocalDateTime currentDateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(" dd-MM-yyyy HH:mm ");
		return currentDateTime.format(formatter);
	}

	private int tableCount = 1;

	public String createTable(List<FieldDefnition> fieldDefinitions, CreateTableRequest createTableRequest) {

		StringBuilder queryBuilder = new StringBuilder();
		String tableName;

		tableName = createTableRequest.getTableName() + "_" + tableCount;

		queryBuilder.append("CREATE TABLE ").append(tableName).append(" (");
		queryBuilder.append("ID SERIAL PRIMARY KEY, ");
		queryBuilder.append("DOC_ID INTEGER, ");
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
		tableCount++;
		return tableName;
	}

	private String getDatabaseType(String fieldType, int maxLength) {
		if (DMSConstant.STRING.equalsIgnoreCase(fieldType)) {
			return "VARCHAR(" + maxLength + ")";
		} else if (DMSConstant.INTEGER.equalsIgnoreCase(fieldType)) {
			return "BIGINT";
		} else {
			return fieldType;
		}
	}

	public ProfMetaDataEntity convertTableReqToMetaEntity(CreateTableRequest createTableRequest, String tableName,
			ProfUserInfoEntity entity) {
		ProfMetaDataEntity metaDataEntity = new ProfMetaDataEntity();
		metaDataEntity.setTableName(tableName);
		metaDataEntity.setFileExtension(createTableRequest.getFileExtension());
		metaDataEntity.setCreatedBy(entity.getUserName());
		metaDataEntity.setCreatedAt(formatCurrentDateTime());
		metaDataEntity.setName(createTableRequest.getTableName());
		metaDataEntity.setStatus("A");
		return metaDataEntity;
	}

	public GetAllTableResponse convertEntityToResponse(ProfMetaDataEntity dataEntity, EntityManager entityManager) {
		GetAllTableResponse allTableResponse = new GetAllTableResponse();
		allTableResponse.setId(dataEntity.getId());
		allTableResponse.setCreatedAt(dataEntity.getCreatedAt());
		allTableResponse.setCreatedBy(dataEntity.getCreatedBy());
		allTableResponse.setFileExtention(dataEntity.getFileExtension());
		allTableResponse.setTableName(dataEntity.getName());
		String tableName = dataEntity.getTableName().toLowerCase();
		if (tableName != null) {
			List<FieldDefinitionResponse> definitionResponses = getColumnDetails(tableName, entityManager);
			allTableResponse.setFieldNames(definitionResponses);
		}
		return allTableResponse;
	}

	private List<FieldDefinitionResponse> getColumnDetails(String tableName, EntityManager entityManager) {
		List<FieldDefinitionResponse> definitionResponses = new ArrayList<>();

		try {
			String sqlQuery = "SELECT column_name, data_type, is_nullable, character_maximum_length "
					+ "FROM information_schema.columns " + "WHERE table_name = :tableName";

			@SuppressWarnings("unchecked")
			List<Object[]> result = entityManager.createNativeQuery(sqlQuery).setParameter("tableName", tableName)
					.getResultList();

			for (Object[] row : result) {
				String columnName = (String) row[0];
				String dataType = (String) row[1];
				String isNullable = (String) row[2];
				Integer characterMaxLength = (Integer) row[3];

				FieldDefinitionResponse fieldDefinitionResponse = new FieldDefinitionResponse();
				fieldDefinitionResponse.setFieldName(columnName);
				List<String> values = fetchDataFromTable(columnName, tableName);
				fieldDefinitionResponse.setValue(String.join(",", values));
				fieldDefinitionResponse
						.setFieldType("character varying".equalsIgnoreCase(dataType) ? "String" : "Integer");
				fieldDefinitionResponse.setMandatory("NO".equalsIgnoreCase(isNullable) ? "Y" : "N");
				fieldDefinitionResponse.setMaxLength(characterMaxLength != null ? characterMaxLength.toString() : null);
				definitionResponses.add(fieldDefinitionResponse);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return definitionResponses;
	}

	private List<String> fetchDataFromTable(String columnName, String tableName) {
		List<String> values = new ArrayList<>();
		try {
			String sqlQuery = "SELECT " + columnName + " FROM " + tableName;
			@SuppressWarnings("unchecked")
			List<Object> results = entityManager.createNativeQuery(sqlQuery).getResultList();
			for (Object result : results) {
				if (result != null) {
					values.add(result.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return values;
	}

	public ProfOverallMetaDataResponse convertToResponse(ProfMetaDataEntity metaDataEntity) {
		ProfOverallMetaDataResponse metaDataResponse = new ProfOverallMetaDataResponse();
		metaDataResponse.setId(metaDataEntity.getId());
		metaDataResponse.setCreatedAt(metaDataEntity.getCreatedAt());
		metaDataResponse.setFileExtenion(metaDataEntity.getFileExtension());
		metaDataResponse.setName(metaDataEntity.getName());
		metaDataResponse.setStatus(metaDataEntity.getStatus());
		metaDataResponse.setTableName(metaDataEntity.getTableName());
		metaDataResponse.setCreatedBy(metaDataEntity.getCreatedBy());
		return metaDataResponse;
	}

	public ProfOverallMetaDataResponse convertMetaEntityToResponse(ProfMetaDataEntity dataEntity) {
		ProfOverallMetaDataResponse dataResponse = new ProfOverallMetaDataResponse();
		dataResponse.setId(dataEntity.getId());
		dataResponse.setName(dataEntity.getName());
		dataResponse.setTableName(dataEntity.getTableName());
		return dataResponse;
	}

	@Transactional
	public ProfMetaDataResponse insertDataIntoTable(String tableName, List<FieldDefnition> fields, Integer id) {
		ProfMetaDataResponse dataResponse = new ProfMetaDataResponse();
		StringBuilder insertQueryBuilder = new StringBuilder();
		insertQueryBuilder.append("INSERT INTO ").append(tableName).append(" (");

		// Append column names
		for (Iterator<FieldDefnition> it = fields.iterator(); it.hasNext();) {
			FieldDefnition field = it.next();
			insertQueryBuilder.append(field.getFieldName());
			if (it.hasNext()) {
				insertQueryBuilder.append(", ");
			}
		}
		insertQueryBuilder.append(", doc_id) VALUES (");

		// Append values
		for (Iterator<FieldDefnition> it = fields.iterator(); it.hasNext();) {
			FieldDefnition fieldValue = it.next();
			insertQueryBuilder.append(getFormattedValue(fieldValue));
			if (it.hasNext()) {
				insertQueryBuilder.append(", ");
			}
		}
		// Append document ID
		insertQueryBuilder.append(", ").append(id).append(")");

		try {
			entityManager.createNativeQuery(insertQueryBuilder.toString()).executeUpdate();
			dataResponse.setStatus(DMSConstant.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			dataResponse.setStatus(DMSConstant.FAILURE);
		}
		return dataResponse;
	}

	private String getFormattedValue(FieldDefnition fieldValue) {
		if (fieldValue.getFieldType().equalsIgnoreCase("Integer")) {
			return fieldValue.getValue();
		} else {
			return "'" + fieldValue.getValue().replace("'", "''") + "'";
		}
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


}
