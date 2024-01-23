package com.proflaut.dms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proflaut.dms.entity.ProfMetaDataEntity;

@Repository
public interface ProfMetaDataRepository extends JpaRepository<ProfMetaDataEntity, Integer>{
}
