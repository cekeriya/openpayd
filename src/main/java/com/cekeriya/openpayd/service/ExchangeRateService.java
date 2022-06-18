package com.cekeriya.openpayd.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ExchangeRateService {
	private final FixerService fixerService;

	public ExchangeRateService(FixerService fixerService) {
		this.fixerService = fixerService;
	}

	public BigDecimal getRate(String sourceCurrency, String targetCurrency) {
		return fixerService.rate(sourceCurrency, targetCurrency).getRates().get(targetCurrency);
	}
}
