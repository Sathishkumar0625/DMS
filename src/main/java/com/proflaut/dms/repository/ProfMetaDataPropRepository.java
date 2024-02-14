package com.proflaut.dms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proflaut.dms.entity.ProfMetaDataPropertiesEntity;

public interface ProfMetaDataPropRepository extends JpaRepository<ProfMetaDataPropertiesEntity, Integer>{

	List<ProfMetaDataPropertiesEntity> findByMetaId(String metaId);

}
