package com.proflaut.dms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proflaut.dms.entity.ProfMailConfigEntity;

@Repository
public interface ProfMailConfigRepository extends JpaRepository<ProfMailConfigEntity, Integer>{

}
