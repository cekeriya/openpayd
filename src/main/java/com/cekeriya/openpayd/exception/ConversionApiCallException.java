package com.cekeriya.openpayd.exception;

import com.cekeriya.openpayd.constant.ErrorType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ConversionApiCallException extends RuntimeException {
	private int code;
	private String message;

	public ConversionApiCallException(ErrorType errorType) {
		this.code = errorType.getCode();
		this.message = errorType.getMessage();
	}
}
