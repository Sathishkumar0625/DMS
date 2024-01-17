package com.proflaut.dms.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
	
@Entity
@Table(name = "PROF_CUSTOMERACCOUNTREQUESTINFO")
public class ProfAccountRequestEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;

	@Column(name = "CUSTOMER_ID")
	private String customerId;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "joinID", referencedColumnName = "id")
	private List<ProfAccountDetailsEntity> profAccountDetailsEntity;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public List<ProfAccountDetailsEntity> getProfAccountDetailsEntity() {
		return profAccountDetailsEntity;
	}

	public void setProfAccountDetailsEntity(List<ProfAccountDetailsEntity> profAccountDetailsEntity) {
		this.profAccountDetailsEntity = profAccountDetailsEntity;
	}

}
