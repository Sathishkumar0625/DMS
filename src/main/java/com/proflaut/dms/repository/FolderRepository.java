package com.proflaut.dms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proflaut.dms.entity.FolderEntity;
import com.proflaut.dms.entity.ProfDocEntity;
import com.proflaut.dms.entity.ProfUserPropertiesEntity;

public interface FolderRepository  extends JpaRepository<FolderEntity, Integer> {

	//FolderEntity findById(Integer id);
	
	Optional<FolderEntity> findById(Integer id);


	FolderEntity findByParentFolderIDAndCustomerId(int parentFolderID, int customerId);
}
