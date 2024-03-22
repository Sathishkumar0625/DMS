package com.proflaut.dms.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "PROF_JOBPACK_TEMPLATE")
public class ProfJobpackTemplate {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private int id;
	@Column(name = "FORM_NAME")
	private String formName;
	@Column(name = "FIELD_NAME", columnDefinition = "TEXT")
	private String fieldName;
}
