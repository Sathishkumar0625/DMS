package com.proflaut.dms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.proflaut.dms.entity.ProfGroupUserMappingEntity;
import com.proflaut.dms.entity.ProfUserGroupMappingEntity;

@Repository
public interface ProfUserGroupMappingRepository extends JpaRepository<ProfUserGroupMappingEntity, Integer>{

	List<ProfUserGroupMappingEntity> findByUserId(int userId);

	ProfUserGroupMappingEntity findByGroupIdAndUserId(String valueOf, int userId);

	List<ProfGroupUserMappingEntity> findByGroupId(int groupId);

	String countByGroupId(String groupId);

	@Query("SELECT userId FROM ProfUserGroupMappingEntity WHERE groupId = :groupId")
    List<Integer> findUserIdsByGroupId(@Param("groupId") String groupId);
}

