package com.proflaut.dms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proflaut.dms.entity.ProfGroupUserMappingEntity;
import com.proflaut.dms.entity.ProfUserGroupMappingEntity;

public interface ProfGroupUserMappingRepository extends JpaRepository<ProfGroupUserMappingEntity, Integer> {

	List<ProfGroupUserMappingEntity> findByGroupId(int groupId);


	ProfGroupUserMappingEntity findByGroupIdAndUserId(int groupId, int userId);

}
