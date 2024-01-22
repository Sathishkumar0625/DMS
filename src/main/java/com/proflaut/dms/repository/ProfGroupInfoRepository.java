package com.proflaut.dms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proflaut.dms.entity.ProfGroupInfoEntity;

public interface ProfGroupInfoRepository  extends JpaRepository<ProfGroupInfoEntity, Integer>{
//	Optional<ProfGroupInfoEntity> findById(Integer id);
}
