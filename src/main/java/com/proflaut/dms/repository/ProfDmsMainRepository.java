package com.proflaut.dms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proflaut.dms.entity.ProfDmsMainEntity;

@Repository
public interface ProfDmsMainRepository extends JpaRepository<ProfDmsMainEntity, Integer> {

	ProfDmsMainEntity findByUserId(int userId);

	List<ProfDmsMainEntity> findByUserIdAndKey(int userId,String key);


	ProfDmsMainEntity findById(int id);

}
