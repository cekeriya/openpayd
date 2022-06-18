package com.cekeriya.openpayd.response.conversion;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
public class ConversionPerformResponse {
	private BigDecimal amount;
	private UUID transactionId;
}
