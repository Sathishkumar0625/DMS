package com.proflaut.dms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proflaut.dms.entity.ProfOtpEntity;

@Repository
public interface ProfOtpEntityRepository extends JpaRepository<ProfOtpEntity, Integer> {

	ProfOtpEntity findByEmailAndOtp(String email, String otp);

}
