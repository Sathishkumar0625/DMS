package com.proflaut.dms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proflaut.dms.entity.ProfActivitiesEntity;

@Repository
public interface ProfActivityRepository extends JpaRepository<ProfActivitiesEntity, Integer>{

	List<ProfActivitiesEntity> findByUserID(int userId);

	

}
