package com.cekeriya.openpayd.response.exchange;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Info {
	private Long timestamp;
	private BigDecimal rate;
}
