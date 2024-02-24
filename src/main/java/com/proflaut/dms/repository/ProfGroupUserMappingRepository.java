package com.proflaut.dms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.proflaut.dms.entity.ProfGroupUserMappingEntity;

public interface ProfGroupUserMappingRepository extends JpaRepository<ProfGroupUserMappingEntity, Integer> {

	List<ProfGroupUserMappingEntity> findByGroupId(int groupId);


	ProfGroupUserMappingEntity findByGroupIdAndUserId(String string, int userId);

	@Query("SELECT userId FROM ProfGroupUserMappingEntity WHERE groupId = :groupId")
	List<Integer> findUserIdsByGroupId(String groupId);

}
