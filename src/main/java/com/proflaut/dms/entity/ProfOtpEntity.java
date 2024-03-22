package com.proflaut.dms.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "PROF_OTP_ENTITY")
public class ProfOtpEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private int id;
	@Column(name = "OTP")
	private String otp;
	@Column(name = "EMAIL")
	private String email;
	@Column(name = "CREATED_AT")
	private LocalDateTime createdAt;
}
