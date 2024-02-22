package com.proflaut.dms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proflaut.dms.entity.ProfMountPointFolderMappingEntity;

public interface ProfMountFolderMappingRepository extends JpaRepository<ProfMountPointFolderMappingEntity, Integer>{

	List<ProfMountPointFolderMappingEntity> findByMountPointId(int id);

}
