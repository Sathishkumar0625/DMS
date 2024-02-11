package com.proflaut.dms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.proflaut.dms.entity.ProfDocEntity;
import com.proflaut.dms.model.FileRequest;

@Repository
public interface ProfDocUploadRepository extends JpaRepository<ProfDocEntity, Integer> {

	ProfDocEntity save(FileRequest fileRequest);

	List<ProfDocEntity> findByCreatedBy(Integer id);

	// ProfDocEntity findByDocName(String dockName);

	@Modifying
	@Query(nativeQuery = true, value = "UPDATE PROF_DOCUMENT_PROPERTY SET document_name = :docName"
			+ " WHERE created_by = :createdBy ")
	void updatedocName(@Param("docName") String dockName, @Param("createdBy") int createdBy);

	// ProfDocEntity findByUserName(String userName);

	ProfDocEntity findByProspectIdAndDocName(String prospectId, String docName);

	ProfDocEntity findById(int id);

	ProfDocEntity findByFolderId(int id);

	List<ProfDocEntity> findByProspectId(String prospectId);

	ProfDocEntity findByDocNameAndProspectId(String dockName, String prospectId);

	ProfDocEntity findByDocName(String docName);

	@Modifying
	@Query(nativeQuery = true, value = "UPDATE PROF_DOCUMENT_PROPERTY SET is_email = :isEmail" + " WHERE id = :id ")
	void updateIsEmail(@Param("isEmail") String isEmail, @Param("id") int id);

	@Modifying
	@Query("UPDATE ProfDocEntity p SET p.emilResId = :emilResId WHERE p.id = :docId")
	void updateEmailResId(@Param("emilResId") String emilResId, @Param("docId") Integer docId);

	ProfDocEntity findByDocNameAndFolderId(String dockName, int folderId);

	ProfDocEntity findByFolderIdAndMetaId(Integer id, int metaId);

}
