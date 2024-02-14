package com.proflaut.dms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proflaut.dms.entity.ProfMetaDataEntity;
import com.proflaut.dms.entity.ProfMetaDataPropertiesEntity;

@Repository
public interface ProfMetaDataRepository extends JpaRepository<ProfMetaDataEntity, Integer> {
	ProfMetaDataEntity findByName(String tableName);

	ProfMetaDataEntity findByIdAndNameIgnoreCase(Integer id, String name);

	ProfMetaDataEntity findByNameIgnoreCase(String name);

	ProfMetaDataEntity findById(int id);

	void save(ProfMetaDataPropertiesEntity dataProperties);
}
