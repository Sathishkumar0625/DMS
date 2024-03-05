package com.proflaut.dms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proflaut.dms.entity.DashboardDataEntity;

public interface DashBoardRepository extends JpaRepository<DashboardDataEntity, Integer> {

	List<DashboardDataEntity> findByUserName(String userName);

}
