package com.proflaut.dms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proflaut.dms.entity.ProfFileBookmarkEntity;

public interface FileBookmarkRepository extends JpaRepository<ProfFileBookmarkEntity, Integer> {

	List<ProfFileBookmarkEntity> findByBookmarkedBy(String userName);

}
