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
public interface ProfDocUploadRepository extends JpaRepository<ProfDocEntity, Integer>{

	ProfDocEntity save(FileRequest fileRequest);

	List<ProfDocEntity> findByCreatedBy(Integer id);

	//ProfDocEntity findByDocName(String dockName);
	
	@Modifying
	@Query(nativeQuery = true,value="UPDATE PROF_DOCUMENT_PROPERTY SET document_name = :docName"
			+ " WHERE created_by = :createdBy ")
	void updatedocName(@Param("docName") String dockName,@Param("createdBy") int createdBy);

	//ProfDocEntity findByUserName(String userName);

	ProfDocEntity findByProspectIdAndDocName(String prospectId, String docName);

}
