package com.proflaut.dms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proflaut.dms.entity.ProfAccessGroupMappingEntity;

public interface ProfAccessGroupMappingRepository extends JpaRepository<ProfAccessGroupMappingEntity, Integer> {
	List<ProfAccessGroupMappingEntity> findById(int id);

}
