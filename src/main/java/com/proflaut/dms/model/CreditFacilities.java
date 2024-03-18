package com.proflaut.dms.model;


import lombok.Data;

@Data
public class CreditFacilities {
	private String type;
	private String amount;
	private String purpose;
	private String primarySecurity;
	private String collateralSecurityOffered;

}
