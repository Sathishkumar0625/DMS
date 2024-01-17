package com.proflaut.dms.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proflaut.dms.entity.ProfDmsHeader;

@Repository
public interface ProfDmsHeaderRepository extends JpaRepository<ProfDmsHeader, Integer> {

	List<ProfDmsHeader> findByKey(String key);
}
