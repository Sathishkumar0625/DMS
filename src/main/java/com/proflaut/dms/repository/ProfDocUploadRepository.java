package com.proflaut.dms.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.proflaut.dms.entity.ProfDocEntity;
import com.proflaut.dms.entity.ProfFileBookmarkEntity;
import com.proflaut.dms.model.FileRequest;

@Repository
public interface ProfDocUploadRepository extends JpaRepository<ProfDocEntity, Integer> {

	ProfDocEntity save(FileRequest fileRequest);

	List<ProfDocEntity> findByCreatedBy(Integer id);

	@Modifying
	@Query(nativeQuery = true, value = "UPDATE PROF_DOCUMENT_PROPERTY SET document_name = :docName"
			+ " WHERE created_by = :createdBy ")
	void updatedocName(@Param("docName") String dockName, @Param("createdBy") int createdBy);

	ProfDocEntity findByProspectIdAndDocName(String prospectId, String docName);

	ProfDocEntity findById(int id);

	List<ProfDocEntity> findByFolderId(int id);

	List<ProfDocEntity> findByProspectId(String prospectId);

	ProfDocEntity findByDocNameAndProspectId(String dockName, String prospectId);

	ProfDocEntity findByDocName(String docName);

	@Modifying
	@Query(nativeQuery = true, value = "UPDATE PROF_DOCUMENT_PROPERTY SET is_email = :isEmail WHERE id = :id ")
	void updateIsEmail(@Param("isEmail") String isEmail, @Param("id") int id);

	ProfDocEntity findByDocNameAndFolderId(String dockName, int folderId);

	ProfDocEntity findByFolderIdAndMetaId(Integer id, int metaId);

	@Modifying
	@Query(nativeQuery = true, value = "UPDATE PROF_DOCUMENT_PROPERTY SET is_email = :isEmail , email_res_id = :emailResId WHERE id = :id ")
	void updateEmailResIdAndIsEmail(@Param("emailResId") String emailResId, @Param("isEmail") String isEmail,
			@Param("id") int id);

	@Query("SELECT p FROM ProfDocEntity p WHERE p.createdBy IN :userNames")
	List<ProfDocEntity> findByCreatedByIn(@Param("userNames") List<String> userNames);

	long countByCreatedBy(String userId);

	List<ProfDocEntity> findByCreatedBy(String userName);

	List<ProfDocEntity> findByCreatedByIgnoreCase(String string);

	List<ProfDocEntity> getById(int i);

	Long getSumFileSizeByFolderId(String folderId);

	@Query("SELECT p FROM ProfDocEntity p WHERE p.folderId IN :folderIds")
	List<ProfDocEntity> findByFolderIdIn(List<Integer> folderIds);

	List<ProfDocEntity> findByIdIn(List<Integer> fileIds);

	List<ProfDocEntity> findByDocNameLike(String docName);

	@Query("SELECT d FROM ProfDocEntity d WHERE LOWER(d.docName) LIKE LOWER(concat('%', :fileName, '%'))")
	List<ProfDocEntity> findByDocNameLikeIgnoreCase(String fileName);

}
