package com.proflaut.dms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proflaut.dms.entity.ProfUserGroupMappingEntity;

@Repository
public interface ProfUserGroupMappingRepository extends JpaRepository<ProfUserGroupMappingEntity, Integer>{

}
