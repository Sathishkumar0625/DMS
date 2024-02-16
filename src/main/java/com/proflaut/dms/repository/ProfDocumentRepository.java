package com.proflaut.dms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proflaut.dms.entity.ProfDocEntity;

public interface ProfDocumentRepository extends JpaRepository<ProfDocEntity, Integer> {
	List<ProfDocEntity> findById(int id);
}
