package com.cekeriya.openpayd.response.error;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ErrorResponse {

	private int code;

	private String status;

	private String message;

	public ErrorResponse(HttpStatus status, int code, String message) {
		this.status = status.name();
		this.code = code;
		this.message = message;
	}
}
