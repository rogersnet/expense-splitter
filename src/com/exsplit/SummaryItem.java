package com.exsplit;

public class SummaryItem {
	private String mFirstName;
	private String mLastName;
	private String mAmount;

	public SummaryItem(String firstName, String lastName, String amount) {
		this.mFirstName = firstName;
		this.mLastName  = lastName;
		this.mAmount    = amount;
	}

	public String getFirstName() {
		return mFirstName;
	}

	public void setFirstName(String firstName) {
		this.mFirstName = firstName;
	}

	public String getLastName() {
		return mLastName;
	}

	public void setLastName(String lastName) {
		this.mLastName = lastName;
	}

	public String getAmount() {
		return mAmount;
	}

	public void setAmount(String amount) {
		this.mAmount = amount;
	}


}
