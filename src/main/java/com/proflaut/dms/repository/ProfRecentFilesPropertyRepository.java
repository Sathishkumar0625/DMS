package com.proflaut.dms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proflaut.dms.entity.ProfRecentFilePropertyEntity;

public interface ProfRecentFilesPropertyRepository extends JpaRepository<ProfRecentFilePropertyEntity, Integer> {

	List<ProfRecentFilePropertyEntity> findAllByOrderByIdDesc();

}
