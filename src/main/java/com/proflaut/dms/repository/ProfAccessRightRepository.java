package com.proflaut.dms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proflaut.dms.entity.ProfAccessRightsEntity;

@Repository
public interface ProfAccessRightRepository extends JpaRepository<ProfAccessRightsEntity, Integer> {
	ProfAccessRightsEntity findById(int id);
}
