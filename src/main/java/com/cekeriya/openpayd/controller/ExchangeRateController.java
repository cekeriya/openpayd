package com.cekeriya.openpayd.controller;

import com.cekeriya.openpayd.response.error.ErrorResponse;
import com.cekeriya.openpayd.response.rate.ExchangeRateGetResponse;
import com.cekeriya.openpayd.service.ExchangeRateService;
import com.cekeriya.openpayd.validator.CurrencyCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;

@Validated
@RestController
@RequestMapping("/exchange-rate")
public class ExchangeRateController {

	private final ExchangeRateService exchangeRateService;

	public ExchangeRateController(ExchangeRateService exchangeRateService) {
		this.exchangeRateService = exchangeRateService;
	}

	@Operation(summary = "Calculates and returns exchange rate according to source and target currency")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successful Response", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ExchangeRateGetResponse.class)) }),
			@ApiResponse(responseCode = "400", description = "Bad Request - If given currencies are not valid, returns INVALID_PARAMETER(10001) code", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
			@ApiResponse(responseCode = "500", description = "Interval Server Error - If any problem occurred during service provider access, returns CONVERSION_API_CALL_ERROR(10003) code", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }) })
	@GetMapping()
	public ResponseEntity<ExchangeRateGetResponse> getExchangeRate(@RequestParam @NotBlank @CurrencyCode String sourceCurrency, @RequestParam @NotBlank @CurrencyCode String targetCurrency) {
		ExchangeRateGetResponse exchangeRateGetResponse = ExchangeRateGetResponse.builder()
				.exchangeRate(exchangeRateService.getRate(sourceCurrency, targetCurrency)).build();
		
		return new ResponseEntity<>(exchangeRateGetResponse, HttpStatus.OK);
	}

}
