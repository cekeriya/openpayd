package com.cekeriya.openpayd.response.fixer;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FixerRateResponse {
	private boolean success;
	private Long timestamp;
	private String base;
	private Date date;
	private Map<String, BigDecimal> rates;
}
