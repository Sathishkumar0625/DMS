package com.proflaut.dms.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proflaut.dms.entity.DashboardDataEntity;

public interface DashBoardRepository extends JpaRepository<DashboardDataEntity, Integer> {

}
