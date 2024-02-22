package com.proflaut.dms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proflaut.dms.entity.ProfLicenseEntity;


@Repository
public interface ProfLicenseRepository extends JpaRepository<ProfLicenseEntity, Integer> {

}
