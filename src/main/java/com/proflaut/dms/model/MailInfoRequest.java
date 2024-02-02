package com.proflaut.dms.model;

import java.util.Arrays;
import java.util.List;

public class MailInfoRequest {
	private String from;
	private List<String> toList;
	private List<String> ccList;
	private String subject;
	private String bodyTemplate;

	public MailInfoRequest(String from, String[] toList, String subject) {
		this.from = from;
		this.toList = Arrays.asList(toList);
		this.subject = subject;
	}

	public MailInfoRequest(String from, List<String> toList, String subject) {
		this.from = from;
		this.toList = toList;
		this.subject = subject;
	}
	

	public List<String> getCcList() {
		return ccList;
	}

	public void setCcList(List<String> ccList) {
		this.ccList = ccList;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public List<String> getToList() {
		return toList;
	}

	public void setToList(List<String> toList) {
		this.toList = toList;
	}

	
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBodyTemplate() {
		return bodyTemplate;
	}

	public void setBodyTemplate(String bodyTemplate) {
		this.bodyTemplate = bodyTemplate;
	}
}
