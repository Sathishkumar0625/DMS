package com.proflaut.dms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.proflaut.dms.entity.FolderEntity;

public interface FolderRepository  extends JpaRepository<FolderEntity, Integer> {

	//FolderEntity findById(Integer id);
	
	Optional<FolderEntity> findById(Integer id);

	FolderEntity findByProspectId(String prospectId);
	
	@Modifying
	@Query(nativeQuery = true, value = "UPDATE PROF_FOLDER SET PARENT_FOLDER_ID = :count, FOLDER_PATH = :path WHERE PROSPECT_ID = :prospectId")
	void updateParentFolderIdAndFolderPath(@Param("count") int count, @Param("prospectId") String prospectId, String path);



	//FolderEntity findByParentFolderIDAndCustomerId(int parentFolderID, int customerId);
}
