package com.cekeriya.openpayd.response.conversion;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
public class ConversionGetResponse {
	private BigDecimal amount;
	private UUID transactionId;
}
