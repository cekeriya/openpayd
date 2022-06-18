package com.cekeriya.openpayd;

import com.cekeriya.openpayd.controller.ConversionController;
import com.cekeriya.openpayd.controller.ExchangeRateController;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class OpenpaydApplicationTests {

	@Test
	void contextLoads(ApplicationContext context) {
		assertNotNull(context);

		ConversionController conversionController = (ConversionController) context.getBean("conversionController");
		ExchangeRateController exchangeRateController = (ExchangeRateController) context.getBean("exchangeRateController");
		assertNotNull(conversionController);
		assertNotNull(exchangeRateController);
	}

}
