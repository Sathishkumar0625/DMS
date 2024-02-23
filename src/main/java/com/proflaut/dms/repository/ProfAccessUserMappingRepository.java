package com.proflaut.dms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proflaut.dms.entity.ProfAccessUserMappingEntity;

public interface ProfAccessUserMappingRepository extends JpaRepository<ProfAccessUserMappingEntity, Integer>{
	List<ProfAccessUserMappingEntity> findById(int id);

	List<ProfAccessUserMappingEntity> findByUserId(String userId);


	ProfAccessUserMappingEntity findByAccessRightsEntityIdAndUserId(int accessId, String valueOf);
}
