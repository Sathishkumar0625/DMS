package com.proflaut.dms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proflaut.dms.entity.ProfAccessGroupMappingEntity;

public interface ProfAccessGroupMappingRepository extends JpaRepository<ProfAccessGroupMappingEntity, Integer> {
	List<ProfAccessGroupMappingEntity> findById(int id);

	List<ProfAccessGroupMappingEntity> findByGroupId(String groupId);

	ProfAccessGroupMappingEntity findByAccessRightsEntityIdAndGroupId(int accessId, String valueOf);

	List<ProfAccessGroupMappingEntity> findByAccessRightsEntityId(int id);

}
