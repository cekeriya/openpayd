package com.cekeriya.openpayd.service.provider;

import com.cekeriya.openpayd.response.exchange.ConvertResponse;
import com.cekeriya.openpayd.response.exchange.RateResponse;

import java.math.BigDecimal;

public interface ExchangeServiceProvider {
	ConvertResponse convert(String sourceCurrency, String targetCurrency, BigDecimal amount);
	RateResponse rate(String sourceCurrency, String targetCurrency);
}
