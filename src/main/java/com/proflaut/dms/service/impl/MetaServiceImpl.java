package com.proflaut.dms.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proflaut.dms.entity.ProfMetaDataEntity;
import com.proflaut.dms.helper.MetaHelper;
import com.proflaut.dms.repository.ProfMetaDataRepository;

@Service
public class MetaServiceImpl {
	@Autowired
	private EntityManager entityManager;
	@Autowired
	ProfMetaDataRepository dataRepository;

	@Autowired
	MetaHelper helper;

	public Map<String, Object> findAllRowsAndColumns(String tableName) {
		Map<String, Object> responseMap = new HashMap<>();
		try {
			ProfMetaDataEntity dataEntity = dataRepository.findByNameIgnoreCase(tableName);
			if (dataEntity != null) {
				String table=dataEntity.getTableName().toLowerCase();
				String sqlQuery = "SELECT * FROM " + table;
				Query query = entityManager.createNativeQuery(sqlQuery);
				@SuppressWarnings("unchecked")
				List<Object[]> resultList = query.getResultList();
				List<String> columnNames = helper.getColumnNames(table);
				responseMap.put("fieldNames", columnNames);
				List<Map<String, Object>> valuesList = new ArrayList<>();
				for (Object[] row : resultList) {
					Map<String, Object> rowMap = new HashMap<>();
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
}
