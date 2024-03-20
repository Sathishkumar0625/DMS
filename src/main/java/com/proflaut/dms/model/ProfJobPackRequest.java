package com.proflaut.dms.model;

import lombok.Data;

@Data
public class ProfJobPackRequest {
	private String lenderName;
	private String borrowerName;
	private String location;
	private String day;
	private String currentDate;
	private String amountInNumbers;
	private String amountInWords;

}
