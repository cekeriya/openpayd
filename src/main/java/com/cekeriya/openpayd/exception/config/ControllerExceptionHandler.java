package com.cekeriya.openpayd.exception.config;

import com.cekeriya.openpayd.exception.ConversionApiCallException;
import com.cekeriya.openpayd.exception.NotFoundException;
import com.cekeriya.openpayd.response.error.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.cekeriya.openpayd.constant.ErrorType.INVALID_PARAMETER;
import static com.cekeriya.openpayd.constant.ErrorType.MISSING_PARAMETER;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
		HttpStatus status = HttpStatus.BAD_REQUEST;

		String message = ex.getConstraintViolations()
				.stream()
				.map(e -> e.getPropertyPath().toString() + " " + e.getMessage())
				.collect(Collectors.joining(", "));

		return new ResponseEntity<>(new ErrorResponse(status, INVALID_PARAMETER.getCode(), message), status);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
		HttpStatus status = HttpStatus.BAD_REQUEST;

		List<String> errors = new ArrayList<>();

		ex.getBindingResult().getFieldErrors().forEach(fieldError -> errors.add(fieldError.getField() + ": " + fieldError.getDefaultMessage()));
		ex.getBindingResult().getGlobalErrors().forEach(objectError -> errors.add(objectError.getObjectName() + ": " + objectError.getDefaultMessage()));

		return new ResponseEntity<>(new ErrorResponse(status, INVALID_PARAMETER.getCode(), errors.toString()), status);
	}

	@ExceptionHandler(MissingPathVariableException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ErrorResponse> handleMissingPathVariableException(MissingPathVariableException ex) {
		HttpStatus status = HttpStatus.BAD_REQUEST;

		String errorMessage = String.format(MISSING_PARAMETER.getMessage(), ex.getParameter());

		return new ResponseEntity<>(new ErrorResponse(status, MISSING_PARAMETER.getCode(), errorMessage), status);
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
		HttpStatus status = HttpStatus.BAD_REQUEST;

		String errorMessage = String.format(INVALID_PARAMETER.getMessage(), ex.getParameterName());

		return new ResponseEntity<>(new ErrorResponse(status, INVALID_PARAMETER.getCode(), errorMessage), status);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
		HttpStatus status = HttpStatus.BAD_REQUEST;

		String errorMessage = String.format(INVALID_PARAMETER.getMessage(), ex.getName());

		return new ResponseEntity<>(new ErrorResponse(status, INVALID_PARAMETER.getCode(), errorMessage), status);
	}

	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex) {
		HttpStatus status = HttpStatus.NOT_FOUND;

		return new ResponseEntity<>(new ErrorResponse(status, ex.getCode(), ex.getMessage()), status);
	}

	@ExceptionHandler(ConversionApiCallException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<ErrorResponse> handleConversionApiCallException(ConversionApiCallException ex) {
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

		return new ResponseEntity<>(new ErrorResponse(status, ex.getCode(), ex.getMessage()), status);
	}

}
