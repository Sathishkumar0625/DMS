package com.proflaut.dms.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


@JsonPropertyOrder({ "name", "address", "phoneNumber", "jointHolders", "nomineeName", "age" })
public class PersonalUserRequest {
	@JsonProperty("name")
	private String name;

	@JsonProperty("address")
	private String address;

	@JsonProperty("phoneNumber")
	private String phoneNumber;

	@JsonProperty("jointHolders")
	private String jointHolders;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getJointHolders() {
		return jointHolders;
	}

	public void setJointHolders(String jointHolders) {
		this.jointHolders = jointHolders;
	}

	public String getNomineeName() {
		return nomineeName;
	}

	public void setNomineeName(String nomineeName) {
		this.nomineeName = nomineeName;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	@JsonProperty("nomineeName")
	private String nomineeName;

	@JsonProperty("age")
	private int age;

}
