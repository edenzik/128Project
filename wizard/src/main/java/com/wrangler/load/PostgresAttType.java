package com.wrangler.load;

public enum PostgresAttType {
	VARCHAR("VARCHAR(100)"),
	INTEGER("INTEGER"),
	DECIMAL("DECIMAL");
	
	private final String value;
	
	private PostgresAttType(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}
	
	public String toString() {
		return getValue();
	}
}
