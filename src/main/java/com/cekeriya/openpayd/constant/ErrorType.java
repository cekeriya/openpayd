package com.cekeriya.openpayd.constant;

public enum ErrorType {
	MISSING_PARAMETER(10000, "Missing %s parameter"),
	INVALID_PARAMETER(10001, "Invalid value for %s parameter"),
	CONVERSION_NOT_FOUND(10002, "Conversion not found"),
	CONVERSION_API_CALL_ERROR(10003,"An error occur during conversion api call");

	private final int code;
	private final String message;

	ErrorType(int code, String messages) {
		this.code = code;
		this.message = messages;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
}
