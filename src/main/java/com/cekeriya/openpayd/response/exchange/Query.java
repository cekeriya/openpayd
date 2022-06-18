package com.cekeriya.openpayd.response.exchange;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Query {
	private String from;
	private String to;
	private BigDecimal amount;
}
