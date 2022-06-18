package com.cekeriya.openpayd.response.exchange;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConvertResponse {
	private boolean success;
	private Query query;
	private Info info;
	private boolean historical;
	private Date date;
	private BigDecimal result;
}
