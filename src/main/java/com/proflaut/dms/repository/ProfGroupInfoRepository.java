package com.proflaut.dms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proflaut.dms.entity.ProfGroupInfoEntity;

public interface ProfGroupInfoRepository extends JpaRepository<ProfGroupInfoEntity, Integer> {

	ProfGroupInfoEntity findByGroupName(String groupName);
	ProfGroupInfoEntity findById(int id);
	ProfGroupInfoEntity findByGroupNameIgnoreCase(String groupName);
	ProfGroupInfoEntity save(Optional<ProfGroupInfoEntity> entity);
}
