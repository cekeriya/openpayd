package com.cekeriya.openpayd.response.exchange;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RateResponse {
	private boolean success;
	private Long timestamp;
	private String base;
	private Date date;
	private Map<String, BigDecimal> rates;
}
