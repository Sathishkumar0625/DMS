package com.proflaut.dms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proflaut.dms.entity.ProfUserInfoEntity;
import com.proflaut.dms.model.UserInfo;

@Repository
public interface ProfUserInfoRepository extends JpaRepository<ProfUserInfoEntity, Integer>{

	ProfUserInfoEntity save(UserInfo userInfo);

	ProfUserInfoEntity findByUserName(String userName);

	ProfUserInfoEntity findByUserId(int userId);

	List<ProfUserInfoEntity> getByUserId(int userId);

}
