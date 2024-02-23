package com.proflaut.dms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.proflaut.dms.entity.ProfUserInfoEntity;
import com.proflaut.dms.model.UserInfo;

@Repository
public interface ProfUserInfoRepository extends JpaRepository<ProfUserInfoEntity, Integer>{

	ProfUserInfoEntity save(UserInfo userInfo);

	ProfUserInfoEntity findByUserName(String userName);

	ProfUserInfoEntity findByUserId(int userId);

	List<ProfUserInfoEntity> getByUserId(int userId);
	@Query("SELECT f FROM ProfUserInfoEntity f WHERE f.userId NOT IN :userIdsAsInt")
	List<ProfUserInfoEntity> findbyUserIdNotIn(@Param("userIdsAsInt")List<Integer> userIdsAsInt);


}
