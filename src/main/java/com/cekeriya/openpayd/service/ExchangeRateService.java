package com.cekeriya.openpayd.service;

import com.cekeriya.openpayd.service.provider.ExchangeServiceProvider;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ExchangeRateService {
	private final ExchangeServiceProvider fixerService;

	public ExchangeRateService(ExchangeServiceProvider fixerService) {
		this.fixerService = fixerService;
	}

	public BigDecimal getRate(String sourceCurrency, String targetCurrency) {
		return fixerService.rate(sourceCurrency, targetCurrency).getRates().get(targetCurrency);
	}
}
