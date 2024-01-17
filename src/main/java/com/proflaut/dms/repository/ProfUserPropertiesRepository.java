package com.proflaut.dms.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proflaut.dms.entity.ProfUserInfoEntity;
import com.proflaut.dms.entity.ProfUserPropertiesEntity;

@Repository
@Transactional
public interface ProfUserPropertiesRepository extends JpaRepository<ProfUserPropertiesEntity, Integer> {

	ProfUserPropertiesEntity save(ProfUserInfoEntity ent);

	void deleteByUserId(Integer userId);

	ProfUserPropertiesEntity findByUserId(Integer userId);

	ProfUserPropertiesEntity findByToken(String token);
}
