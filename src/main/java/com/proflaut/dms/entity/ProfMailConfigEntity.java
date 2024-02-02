package com.proflaut.dms.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PROF_MAIL_CONFIG")
public class ProfMailConfigEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "EMAIL_RES_ID")
	private int id;
	@Column(name = "SENDER")
	private String sender;
	@Column(name = "RECEIVER")
	private String receiver;
	@Column(name = "SEND_AT")
	private String sendAt;
	@Column(name = "IS_EMAIL")
	private String isEmail;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getSendAt() {
		return sendAt;
	}

	public void setSendAt(String sendAt) {
		this.sendAt = sendAt;
	}

	public String getIsEmail() {
		return isEmail;
	}

	public void setIsEmail(String isEmail) {
		this.isEmail = isEmail;
	}

}
