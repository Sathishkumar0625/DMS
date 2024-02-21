package com.proflaut.dms.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proflaut.dms.entity.ProfMountPointEntity;

public interface ProfMountPointRepository extends JpaRepository<ProfMountPointEntity, Integer>{
	ProfMountPointEntity findById(int id);
}
