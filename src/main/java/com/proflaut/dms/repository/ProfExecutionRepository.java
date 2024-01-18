package com.proflaut.dms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proflaut.dms.entity.ProfExecutionEntity;

@Repository
public interface ProfExecutionRepository extends JpaRepository<ProfExecutionEntity, Integer>{

	List<ProfExecutionEntity> findByActivityName(String key);

	

}
