package com.cekeriya.openpayd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ExchangeRateService {
	@Autowired
	private FixerService fixerService;

	public BigDecimal getRate(String sourceCurrency, String targetCurrency) {
		return fixerService.rate(sourceCurrency, targetCurrency).getRates().get(targetCurrency);
	}
}
