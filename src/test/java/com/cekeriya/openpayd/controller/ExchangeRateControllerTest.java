package com.cekeriya.openpayd.controller;

import com.cekeriya.openpayd.constant.ErrorType;
import com.cekeriya.openpayd.exception.ConversionApiCallException;
import com.cekeriya.openpayd.response.rate.ExchangeRateGetResponse;
import com.cekeriya.openpayd.service.ExchangeRateService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExchangeRateController.class)
public class ExchangeRateControllerTest {
	private static final String SOURCE_CURRENCY = "sourceCurrency";
	private static final String TARGET_CURRENCY = "targetCurrency";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private ExchangeRateService mockExchangeRateService;

	@BeforeEach
	void setUp() {

	}

	@AfterEach
	void tearDown() {
	}

	@Test
	void getExchangeRate_success() throws Exception {
		// prepare
		ExchangeRateGetResponse exchangeRateGetResponse = createExchangeRateGetResponse();

		// mock
		Mockito.when(mockExchangeRateService.getRate(anyString(),anyString())).thenReturn(exchangeRateGetResponse.getExchangeRate());

		// test
		mockMvc.perform(get(String.format("/exchange-rate"))
						.param(SOURCE_CURRENCY, "TRY")
						.param(TARGET_CURRENCY, "USD")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(exchangeRateGetResponse)));
	}

	@Test
	void getExchangeRate_conversionApiCallException() throws Exception {
		// prepare
		ConversionApiCallException conversionApiCallException = new ConversionApiCallException(ErrorType.CONVERSION_API_CALL_ERROR);

		// mock
		Mockito.when(mockExchangeRateService.getRate(anyString(),anyString())).thenThrow(conversionApiCallException);

		// test
		mockMvc.perform(get(String.format("/exchange-rate"))
						.param(SOURCE_CURRENCY, "TRY")
						.param(TARGET_CURRENCY, "USD")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError())
				.andExpect(content().string(containsString(String.valueOf(conversionApiCallException.getCode()))))
				.andExpect(content().string(containsString(conversionApiCallException.getMessage())));
	}

	private ExchangeRateGetResponse createExchangeRateGetResponse() {
		return ExchangeRateGetResponse.builder()
				.exchangeRate(new BigDecimal(100))
				.build();
	}
}