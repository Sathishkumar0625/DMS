package com.proflaut.dms.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proflaut.dms.entity.ProfCheckInAndOutEntity;

public interface ProfCheckInAndOutRepository extends JpaRepository<ProfCheckInAndOutEntity, Integer> {

	ProfCheckInAndOutEntity findByFolderIdAndFolderNameAndUserId(int id, String folderName, Integer userId);

}
