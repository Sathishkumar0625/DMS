package com.proflaut.dms.helper;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MetaHelper {
	@Autowired
	private EntityManager entityManager;

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
