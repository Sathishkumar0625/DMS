package com.proflaut.dms.model;

public class BankingCreditFacilities {
	private String type;
	private String limit;
	private String outstanding;
	private String bankNameAndBranch;
	private String securities;
	private String interestRate;
	private String repaymentTerms;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLimit() {
		return limit;
	}

	public void setLimit(String limit) {
		this.limit = limit;
	}

	public String getOutstanding() {
		return outstanding;
	}

	public void setOutstanding(String outstanding) {
		this.outstanding = outstanding;
	}

	public String getBankNameAndBranch() {
		return bankNameAndBranch;
	}

	public void setBankNameAndBranch(String bankNameAndBranch) {
		this.bankNameAndBranch = bankNameAndBranch;
	}

	public String getSecurities() {
		return securities;
	}

	public void setSecurities(String securities) {
		this.securities = securities;
	}

	public String getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(String interestRate) {
		this.interestRate = interestRate;
	}

	public String getRepaymentTerms() {
		return repaymentTerms;
	}

	public void setRepaymentTerms(String repaymentTerms) {
		this.repaymentTerms = repaymentTerms;
	}

}
