package com.proflaut.dms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proflaut.dms.entity.ProfLanguageConverterEntity;

@Repository
public interface LangRepository extends JpaRepository<ProfLanguageConverterEntity, Integer>{

}
