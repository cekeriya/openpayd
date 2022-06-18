package com.cekeriya.openpayd.response.fixer;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FixerConvertResponse {
	private boolean success;
	private FixerQuery query;
	private FixerInfo info;
	private boolean historical;
	private Date date;
	private BigDecimal result;
}
