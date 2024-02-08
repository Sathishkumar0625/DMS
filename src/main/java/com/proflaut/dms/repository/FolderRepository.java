package com.proflaut.dms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.proflaut.dms.entity.FolderEntity;

@Repository
public interface FolderRepository extends JpaRepository<FolderEntity, Integer> {

	FolderEntity findById(int folderId);

	FolderEntity findByProspectId(String prospectId);

	@Modifying
	@Query(value = "UPDATE PROF_FOLDER SET PARENT_FOLDER_ID = :count WHERE PROSPECT_ID = :prospectId", nativeQuery = true)
	void updateParentFolderIdAndFolderPath(@Param("count") int count, @Param("prospectId") String prospectId);
	
	@Modifying
	@Query(value = "UPDATE PROF_FOLDER SET PARENT_FOLDER_ID = :count WHERE ID = :id", nativeQuery = true)
	void updateParentFolderId(int count, int id);

}
