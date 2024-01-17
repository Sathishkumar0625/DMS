package com.proflaut.dms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proflaut.dms.entity.ProfAccountRequestEntity;

@Repository
public interface ProfAccountRegisterRepository extends JpaRepository<ProfAccountRequestEntity, Long> {

}
