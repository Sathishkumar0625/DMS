package com.proflaut.dms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proflaut.dms.entity.ProfOldImageEntity;

@Repository
public interface ProfOldImageRepository extends  JpaRepository<ProfOldImageEntity, Integer>{

}
