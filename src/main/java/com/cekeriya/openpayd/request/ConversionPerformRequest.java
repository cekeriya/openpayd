package com.cekeriya.openpayd.request;

import com.cekeriya.openpayd.validator.CurrencyCode;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class ConversionPerformRequest {
	@NotBlank
	@CurrencyCode
	private String sourceCurrency;

	@NotNull
	@Positive
	private BigDecimal sourceAmount;

	@NotBlank
	@CurrencyCode
	private String targetCurrency;
}
