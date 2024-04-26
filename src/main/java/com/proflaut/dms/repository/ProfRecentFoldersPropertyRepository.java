package com.proflaut.dms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proflaut.dms.entity.ProfRecentFolderPropertyEntity;

public interface ProfRecentFoldersPropertyRepository extends JpaRepository<ProfRecentFolderPropertyEntity, Integer>{

	List<ProfRecentFolderPropertyEntity> findAllByOrderByIdDesc();

}
