package com.proflaut.dms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proflaut.dms.entity.ProfDownloadHistoryEntity;

public interface ProfDownloadHistoryRepo extends JpaRepository<ProfDownloadHistoryEntity, Integer>{

	List<ProfDownloadHistoryEntity> findByUserId(int userId);

	List<ProfDownloadHistoryEntity> findByUserName(String userName);

}
