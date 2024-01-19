package com.proflaut.dms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.proflaut.dms.entity.ProfExecutionEntity;

@Repository
public interface ProfExecutionRepository extends JpaRepository<ProfExecutionEntity, Integer> {

	List<ProfExecutionEntity> findByActivityName(String key);

	@Query(value = "SELECT e.prospect_id AS e_prospect_id, d.prospect_id AS d_prospect_id "
			+ "FROM PROF_EXCECUTION e JOIN PROF_DMS_MAIN d ON e.prospect_id = d.prospect_id "
			+ "WHERE e.activity_name = :key", nativeQuery = true)
	List<Object[]> joinQuery(@Param("key") String key);

}
