package com.proflaut.dms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proflaut.dms.entity.ProfFolderBookMarkEntity;

@Repository
public interface BookmarkRepository extends JpaRepository<ProfFolderBookMarkEntity, Integer>{

	List<ProfFolderBookMarkEntity> findByBookMarkedBy(String userName);

	ProfFolderBookMarkEntity findByFolderId(int id);

}
