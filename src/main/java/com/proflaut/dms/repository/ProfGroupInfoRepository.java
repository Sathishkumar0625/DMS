package com.proflaut.dms.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proflaut.dms.entity.ProfGroupInfoEntity;

public interface ProfGroupInfoRepository extends JpaRepository<ProfGroupInfoEntity, Integer> {

	ProfGroupInfoEntity findByGroupName(String groupName);

	ProfGroupInfoEntity findByGroupNameIgnoreCase(String groupName);
}
