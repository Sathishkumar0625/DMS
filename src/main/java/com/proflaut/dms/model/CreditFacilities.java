package com.proflaut.dms.model;

public class CreditFacilities {
	private String type;
	private String amount;
	private String purpose;
	private String primarySecurity;
	private String collateralSecurityOffered;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getPrimarySecurity() {
		return primarySecurity;
	}

	public void setPrimarySecurity(String primarySecurity) {
		this.primarySecurity = primarySecurity;
	}

	public String getCollateralSecurityOffered() {
		return collateralSecurityOffered;
	}

	public void setCollateralSecurityOffered(String collateralSecurityOffered) {
		this.collateralSecurityOffered = collateralSecurityOffered;
	}

}
