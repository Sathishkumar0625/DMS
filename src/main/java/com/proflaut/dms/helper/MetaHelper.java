package com.proflaut.dms.helper;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.entity.ProfDocEntity;
import com.proflaut.dms.entity.ProfMetaDataEntity;
import com.proflaut.dms.entity.ProfMetaDataPropertiesEntity;
import com.proflaut.dms.entity.ProfUserInfoEntity;
import com.proflaut.dms.model.CreateTableRequest;
import com.proflaut.dms.model.FieldDefinitionResponse;
import com.proflaut.dms.model.FieldDefnition;
import com.proflaut.dms.model.GetAllTableResponse;
import com.proflaut.dms.model.ProfMetaDataResponse;
import com.proflaut.dms.model.ProfOverallMetaDataResponse;
import com.proflaut.dms.repository.ProfMetaDataPropRepository;
import com.proflaut.dms.service.impl.MetaServiceImpl;

@Component
public class MetaHelper {

	private static final Logger logger = LogManager.getLogger(MetaHelper.class);

	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	MetaServiceImpl metaServiceImpl;

	@Autowired
	ProfMetaDataPropRepository dataPropRepository;

	public String formatCurrentDateTime() {
		LocalDateTime currentDateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(" dd-MM-yyyy HH:mm ");
		return currentDateTime.format(formatter);
	}

	public String createTable(List<FieldDefnition> fieldDefinitions, CreateTableRequest createTableRequest, int id) {

		StringBuilder queryBuilder = new StringBuilder();
		String tableName;
		tableName = createTableRequest.getTableName().replace(" ", "_") + "_" + id;

		queryBuilder.append("CREATE TABLE ").append(tableName).append(" (");
		queryBuilder.append("ID SERIAL PRIMARY KEY, ");
		queryBuilder.append("DOC_ID INTEGER, ");
		for (Iterator<FieldDefnition> it = fieldDefinitions.iterator(); it.hasNext();) {
			FieldDefnition field = it.next();
			String originalFieldName = field.getFieldName();
			String fieldName = originalFieldName.replace(" ", "_");
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
		return tableName;

	}

	private String getDatabaseType(String fieldType, int maxLength) {
		if (DMSConstant.STRING.equalsIgnoreCase(fieldType)) {
			return "VARCHAR(" + maxLength + ")";
		} else if (DMSConstant.INTEGER.equalsIgnoreCase(fieldType)) {
			return "Integer";
		} else {
			return fieldType;
		}
	}

	public ProfMetaDataEntity convertTableReqToMetaEntity(CreateTableRequest createTableRequest,
			ProfUserInfoEntity entity) {
		ProfMetaDataEntity metaDataEntity = new ProfMetaDataEntity();
		metaDataEntity.setFileExtension(createTableRequest.getFileExtension());
		metaDataEntity.setCreatedBy(entity.getUserName());
		metaDataEntity.setCreatedAt(formatCurrentDateTime());
		metaDataEntity.setName(createTableRequest.getTableName());
		metaDataEntity.setStatus("A");
		return metaDataEntity;
	}

	public GetAllTableResponse convertEntityToResponse(ProfMetaDataEntity dataEntity, int docId) {
		GetAllTableResponse allTableResponse = new GetAllTableResponse();
		allTableResponse.setId(dataEntity.getId());
		allTableResponse.setCreatedAt(dataEntity.getCreatedAt());
		allTableResponse.setCreatedBy(dataEntity.getCreatedBy());
		allTableResponse.setFileExtention(dataEntity.getFileExtension());
		allTableResponse.setTableName(dataEntity.getName());
		String tableName = dataEntity.getTableName();
		if (tableName != null) {
			List<FieldDefinitionResponse> definitionResponses = getColumnDetails(tableName, dataEntity, docId);
			allTableResponse.setFieldNames(definitionResponses);
		}
		return allTableResponse;
	}

	private List<FieldDefinitionResponse> getColumnDetails(String tableName, ProfMetaDataEntity dataEntity, int docId) {
		List<FieldDefinitionResponse> definitionResponses = new ArrayList<>();
		try {
			List<ProfMetaDataPropertiesEntity> dataPropertiesEntities = dataPropRepository
					.findByMetaId(String.valueOf(dataEntity.getId()));

			for (ProfMetaDataPropertiesEntity property : dataPropertiesEntities) {
				FieldDefinitionResponse fieldDefinitionResponse = new FieldDefinitionResponse();
				String fieldName = property.getFieldNames().replace("_", " ");
				fieldDefinitionResponse.setFieldName(fieldName);
				fieldDefinitionResponse.setFieldType(property.getFieldType());
				fieldDefinitionResponse.setMandatory(property.getMandatory());
				fieldDefinitionResponse.setMaxLength(String.valueOf(property.getLength()));
				List<String> values = fetchDataFromTable(property.getFieldNames(), tableName, docId);
				fieldDefinitionResponse.setValue(String.join(",", values));

				definitionResponses.add(fieldDefinitionResponse);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return definitionResponses;
	}

	private List<String> fetchDataFromTable(String columnName, String tableName, int docId) {
		List<String> values = new ArrayList<>();
		try {
			String column = columnName.replace(" ", "_");
			String sqlQuery = "SELECT " + column + " FROM " + tableName + " WHERE doc_id = :docId";

			@SuppressWarnings("unchecked")
			List<Object> results = entityManager.createNativeQuery(sqlQuery).setParameter("docId", docId)
					.getResultList();
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
	public ProfMetaDataResponse insertDataIntoTable(String tableName, List<FieldDefnition> fields, Integer id,
			Path path, ProfDocEntity docEntity) throws IOException {
		int docId = docEntity.getId();
		ProfMetaDataResponse dataResponse = new ProfMetaDataResponse();
		StringBuilder insertQueryBuilder = buildInsertQueryBuilder(tableName, fields, id);

		try {
			if (recordExists(tableName, docId)) {
				performUpdate(tableName, fields, docId);
				logger.info("Data Updated SuccessFully -> {}", tableName);

			} else {
				performInsert(insertQueryBuilder);
			}
			dataResponse.setStatus(DMSConstant.SUCCESS);
		} catch (Exception e) {
			handleException(e, path);
			dataResponse.setStatus(DMSConstant.FAILURE);
		}
		return dataResponse;
	}

	private StringBuilder buildInsertQueryBuilder(String tableName, List<FieldDefnition> fields, Integer id) {
		StringBuilder insertQueryBuilder = new StringBuilder();
		insertQueryBuilder.append("INSERT INTO ").append(tableName).append(" (");

		// Append column names
		for (Iterator<FieldDefnition> it = fields.iterator(); it.hasNext();) {
			FieldDefnition field = it.next();
			insertQueryBuilder.append(field.getFieldName().replace(" ", "_"));
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

		return insertQueryBuilder;
	}

	private boolean recordExists(String tableName, int docId) {
		String docIdColumnName = "doc_id";
		String selectQuery = "SELECT COUNT(*) FROM " + tableName + " WHERE " + docIdColumnName + " = :docId";
		Query selectQueryObject = entityManager.createNativeQuery(selectQuery);
		selectQueryObject.setParameter("docId", Integer.valueOf(docId));
		BigInteger count = (BigInteger) selectQueryObject.getSingleResult();
		return count.intValue() > 0;
	}

	private void performUpdate(String tableName, List<FieldDefnition> fields, int docId) {
		String docIdColumnName = "doc_id";
		StringBuilder updateQueryBuilder = new StringBuilder();
		updateQueryBuilder.append("UPDATE ").append(tableName).append(" SET ");
		for (Iterator<FieldDefnition> it = fields.iterator(); it.hasNext();) {
			FieldDefnition fieldValue = it.next();
			updateQueryBuilder.append(fieldValue.getFieldName().replace(" ", "_")).append(getFormattedValue(fieldValue));
			if (it.hasNext()) {
				updateQueryBuilder.append(", ");
			}
		}
		updateQueryBuilder.append(" WHERE ").append(docIdColumnName).append(" = ").append(docId);
		entityManager.createNativeQuery(updateQueryBuilder.toString()).executeUpdate();
	}

	private void performInsert(StringBuilder insertQueryBuilder) {
		entityManager.createNativeQuery(insertQueryBuilder.toString()).executeUpdate();
	}

	private void handleException(Exception e, Path path) throws IOException {
		if (path != null) {
			metaServiceImpl.delete(path);
		}
		e.printStackTrace();
	}

	private String getFormattedValue(FieldDefnition fieldValue) {
		if (fieldValue.getFieldType().equalsIgnoreCase("Integer")) {
			return fieldValue.getValue();
		} else {
			return "'" + fieldValue.getValue().replace("'", "''") + "'";
		}
	}

	public List<String> getColumnNames(String tableName) {
		List<String> columnNames = new ArrayList<>();
		try {
			String sqlQuery = "SELECT column_name FROM information_schema.columns WHERE table_name = :tableName";
			Query query = entityManager.createNativeQuery(sqlQuery);
			query.setParameter("tableName", tableName);
			@SuppressWarnings("unchecked")
			List<String> rawColumnNames = query.getResultList();
			for (String rawColumnName : rawColumnNames) {
				String escapedColumnName = "\"" + rawColumnName + "\"";
				columnNames.add(escapedColumnName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return columnNames;
	}

	public List<ProfMetaDataPropertiesEntity> convertMetaEntityToMetaProperties(CreateTableRequest createTableRequest,
			ProfMetaDataEntity dataEnt) {
		List<ProfMetaDataPropertiesEntity> metaDataPropertiesList = new ArrayList<>();

		for (FieldDefnition field : createTableRequest.getFields()) {
			ProfMetaDataPropertiesEntity metaDataProperties = new ProfMetaDataPropertiesEntity();
			metaDataProperties.setMetaId(String.valueOf(dataEnt.getId()));
			String originalFieldName = field.getFieldName().replace(" ", "_");
			metaDataProperties.setFieldNames(originalFieldName);
			metaDataProperties.setFieldType(field.getFieldType());
			metaDataProperties.setMandatory(field.getMandatory());
			metaDataProperties.setLength(Integer.parseInt(field.getMaxLength()));
			metaDataPropertiesList.add(metaDataProperties);
		}

		return metaDataPropertiesList;
	}

	public GetAllTableResponse convertEntityToGetAllTableResponse(ProfMetaDataEntity dataEntity) {
		GetAllTableResponse allTableResponse = new GetAllTableResponse();
		allTableResponse.setId(dataEntity.getId());
		allTableResponse.setCreatedAt(dataEntity.getCreatedAt());
		allTableResponse.setCreatedBy(dataEntity.getCreatedBy());
		allTableResponse.setFileExtention(dataEntity.getFileExtension());
		allTableResponse.setTableName(dataEntity.getName());
		String tableName = dataEntity.getTableName();
		if (tableName != null) {
			List<FieldDefinitionResponse> definitionResponses = getColumnDetailsForRespose(tableName, dataEntity);
			allTableResponse.setFieldNames(definitionResponses);
		}
		return allTableResponse;
	}

	private List<FieldDefinitionResponse> getColumnDetailsForRespose(String tableName, ProfMetaDataEntity dataEntity) {

		List<FieldDefinitionResponse> definitionResponses = new ArrayList<>();
		try {
			List<ProfMetaDataPropertiesEntity> dataPropertiesEntities = dataPropRepository
					.findByMetaId(String.valueOf(dataEntity.getId()));

			for (ProfMetaDataPropertiesEntity property : dataPropertiesEntities) {
				FieldDefinitionResponse fieldDefinitionResponse = new FieldDefinitionResponse();
				String fieldName = property.getFieldNames().replace("_", " ");
				fieldDefinitionResponse.setFieldName(fieldName);
				fieldDefinitionResponse.setFieldType(property.getFieldType());
				fieldDefinitionResponse.setMandatory(property.getMandatory());
				fieldDefinitionResponse.setMaxLength(String.valueOf(property.getLength()));
				List<String> values = fetchDataFromMetaTable(property.getFieldNames(), tableName);
				fieldDefinitionResponse.setValue(String.join(",", values));

				definitionResponses.add(fieldDefinitionResponse);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return definitionResponses;
	}

	private List<String> fetchDataFromMetaTable(String columnName, String tableName) {
		List<String> values = new ArrayList<>();
		String column = columnName.replace(" ", "_");
		try {
			String sqlQuery = "SELECT " + column + " FROM " + tableName;

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

}
