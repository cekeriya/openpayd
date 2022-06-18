package com.cekeriya.openpayd.controller;

import com.cekeriya.openpayd.response.rate.ExchangeRateGetResponse;
import com.cekeriya.openpayd.service.ExchangeRateService;
import com.cekeriya.openpayd.validator.CurrencyCode;
import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	private ExchangeRateService exchangeRateService;


	@GetMapping()
	public ResponseEntity<?> getExchangeRate(@RequestParam @NotBlank @CurrencyCode String sourceCurrency, @RequestParam @NotBlank @CurrencyCode String targetCurrency) {
		ExchangeRateGetResponse exchangeRateGetResponse = ExchangeRateGetResponse.builder()
				.exchangeRate(exchangeRateService.getRate(sourceCurrency, targetCurrency)).build();
		
		return new ResponseEntity<>(exchangeRateGetResponse, HttpStatus.OK);
	}

}
