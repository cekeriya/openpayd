package com.cekeriya.openpayd.response.rate;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class ExchangeRateGetResponse {
	private BigDecimal exchangeRate;
}
