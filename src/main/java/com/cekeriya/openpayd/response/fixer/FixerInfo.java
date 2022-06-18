package com.cekeriya.openpayd.response.fixer;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FixerInfo {
	private Long timestamp;
	private BigDecimal rate;
}
