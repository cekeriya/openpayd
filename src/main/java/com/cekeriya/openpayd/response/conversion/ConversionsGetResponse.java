package com.cekeriya.openpayd.response.conversion;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Builder
public class ConversionsGetResponse {
	private BigDecimal amount;
	private UUID transactionId;
	private String targetCurrency;
	private String sourceCurrency;
	private Date conversionDate;
	private Date createdDate;
}
