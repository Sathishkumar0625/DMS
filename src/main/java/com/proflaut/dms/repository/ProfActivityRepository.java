package com.proflaut.dms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proflaut.dms.entity.ProfActivitiesEntity;

public interface ProfActivityRepository extends JpaRepository<ProfActivitiesEntity, Integer>{

	List<ProfActivitiesEntity> findByUserID(int userId);

	

}
