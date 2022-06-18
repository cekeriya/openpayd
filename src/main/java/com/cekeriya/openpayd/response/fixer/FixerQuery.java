package com.cekeriya.openpayd.response.fixer;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FixerQuery {
	private String from;
	private String to;
	private BigDecimal amount;
}
