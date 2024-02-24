package com.proflaut.dms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.proflaut.dms.entity.ProfGroupInfoEntity;

public interface ProfGroupInfoRepository extends JpaRepository<ProfGroupInfoEntity, Integer> {

	ProfGroupInfoEntity findByGroupName(String groupName);

	ProfGroupInfoEntity findById(int id);

	ProfGroupInfoEntity findByGroupNameIgnoreCase(String groupName);

	ProfGroupInfoEntity save(Optional<ProfGroupInfoEntity> entity);

	List<ProfGroupInfoEntity> findByUserId(int userId);

	List<ProfGroupInfoEntity> getById(int groupId);

	@Query("SELECT f FROM ProfGroupInfoEntity f WHERE f.id NOT IN :groupId")
	List<ProfGroupInfoEntity> findbyIdNotIn(List<Integer> groupId);

	long countByUserId(int userId);

}
