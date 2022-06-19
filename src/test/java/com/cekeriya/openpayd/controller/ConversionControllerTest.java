package com.cekeriya.openpayd.controller;

import com.cekeriya.openpayd.constant.ErrorType;
import com.cekeriya.openpayd.exception.ConversionApiCallException;
import com.cekeriya.openpayd.exception.NotFoundException;
import com.cekeriya.openpayd.mapper.ConversionMapper;
import com.cekeriya.openpayd.model.Conversion;
import com.cekeriya.openpayd.request.ConversionPerformRequest;
import com.cekeriya.openpayd.response.conversion.ConversionGetResponse;
import com.cekeriya.openpayd.response.conversion.ConversionPerformResponse;
import com.cekeriya.openpayd.response.conversion.ConversionsGetResponse;
import com.cekeriya.openpayd.service.ConversionService;
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
import java.util.*;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConversionController.class)
public class ConversionControllerTest {
	private static final String TRANSACTION_ID = "transactionId";
	private static final String CONVERSION_DATE = "conversionDate";
	private static final String PAGE = "page";
	private static final String SIZE = "size";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private ConversionService mockConversionService;

	@MockBean
	private ConversionMapper mockConversionMapper;

	@BeforeEach
	void setUp() {

	}

	@AfterEach
	void tearDown() {
	}

	@Test
	void getConversions_transactionId_success() throws Exception {
		// prepare
		Conversion conversion = createConversionObject();
		ConversionGetResponse conversionGetResponse = createConversionGetResponse(conversion);

		// mock
		Mockito.when(mockConversionService.findByTransactionId(any(UUID.class))).thenReturn(conversion);
		Mockito.when(mockConversionMapper.toConversionResponse(any(Conversion.class))).thenReturn(conversionGetResponse);

		// test
		mockMvc.perform(get(String.format("/conversions"))
						.param(TRANSACTION_ID, conversion.getTransactionId().toString())
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(Arrays.asList(conversionGetResponse))));
	}

	@Test
	void getConversions_transactionId_notFound() throws Exception {
		// prepare
		NotFoundException notFoundException = new NotFoundException(ErrorType.CONVERSION_NOT_FOUND);

		// mock
		Mockito.when(mockConversionService.findByTransactionId(any(UUID.class))).thenThrow(notFoundException);

		// test
		mockMvc.perform(get(String.format("/conversions"))
						.param(TRANSACTION_ID, UUID.randomUUID().toString())
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(content().string(containsString(String.valueOf(notFoundException.getCode()))))
				.andExpect(content().string(containsString(notFoundException.getMessage())));
	}

	@Test
	void getConversions_transactionId_validationError() throws Exception {
		// test
		mockMvc.perform(get(String.format("/conversions"))
						.param(TRANSACTION_ID, "invalid_uuid")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(containsString(String.valueOf(ErrorType.INVALID_PARAMETER.getCode()))))
				.andExpect(content().string(containsString(String.format(ErrorType.INVALID_PARAMETER.getMessage(), "transactionId"))));
	}

	@Test
	void getConversions_conversionDate_success() throws Exception {
		// prepare
		List<Conversion> conversionList = new ArrayList<>();
		conversionList.add(createConversionObject());
		conversionList.add(createConversionObject());
		conversionList.add(createConversionObject());
		conversionList.add(createConversionObject());

		List<ConversionsGetResponse> conversionsGetResponseList = createConversionGetResponse(conversionList);

		// mock
		Mockito.when(mockConversionService.findByConversionDate(any(Date.class), anyInt(), anyInt())).thenReturn(conversionList);
		Mockito.when(mockConversionMapper.toConversionResponse(anyList())).thenReturn(conversionsGetResponseList);

		// test
		mockMvc.perform(get(String.format("/conversions"))
						.param(CONVERSION_DATE, "2022-06-16")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(conversionsGetResponseList)));
	}

	@Test
	void getConversions_conversionDate_pagination_success() throws Exception {
		// prepare
		List<Conversion> conversionList = new ArrayList<>();
		conversionList.add(createConversionObject());
		conversionList.add(createConversionObject());
		conversionList.add(createConversionObject());
		conversionList.add(createConversionObject());
		conversionList.stream().sorted(Comparator.comparing(Conversion::getLastModifiedDate));

		int size = 3;
		List<ConversionsGetResponse> conversionsGetResponseList = createConversionGetResponse(conversionList.subList(0, size - 1));

		// mock
		Mockito.when(mockConversionService.findByConversionDate(any(Date.class), anyInt(), anyInt())).thenReturn(conversionList.subList(0, size - 1));
		Mockito.when(mockConversionMapper.toConversionResponse(anyList())).thenReturn(conversionsGetResponseList);

		// test
		mockMvc.perform(get(String.format("/conversions"))
						.param(PAGE, "0")
						.param(SIZE, String.valueOf(size))
						.param(CONVERSION_DATE, "2022-06-16")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(conversionsGetResponseList)));
	}

	@Test
	void getConversions_conversionDate_pagination_validationError() throws Exception {
		// test
		mockMvc.perform(get(String.format("/conversions"))
						.param(PAGE, "-10")
						.param(SIZE, "-20")
						.param(CONVERSION_DATE, "2022-06-16")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(containsString(String.valueOf(ErrorType.INVALID_PARAMETER.getCode()))))
				.andExpect(content().string(containsString("getConversions.size must be greater than 0")))
				.andExpect(content().string(containsString("getConversions.page must be greater than or equal to 0")));
	}

	@Test
	void performConversions_success() throws Exception {
		// prepare
		Conversion conversion = createConversionObject();
		ConversionPerformRequest conversionPerformRequest = createConversionPerformRequest(conversion);
		ConversionPerformResponse conversionPerformResponse = createConversionPerformResponse(conversion);

		// mock
		Mockito.when(mockConversionService.performConversion(any(ConversionPerformRequest.class))).thenReturn(conversion);
		Mockito.when(mockConversionMapper.toConversionPerformResponse(any(Conversion.class))).thenReturn(conversionPerformResponse);

		// test
		mockMvc.perform(post(String.format("/conversions"))
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(conversionPerformRequest)))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(conversionPerformResponse)));
	}

	@Test
	void performConversions_conversionApiCallError() throws Exception {
		// prepare
		ConversionApiCallException conversionApiCallException = new ConversionApiCallException(ErrorType.CONVERSION_API_CALL_ERROR);
		Conversion conversion = createConversionObject();
		ConversionPerformRequest conversionPerformRequest = createConversionPerformRequest(conversion);

		// mock
		Mockito.when(mockConversionService.performConversion(any(ConversionPerformRequest.class))).thenThrow(conversionApiCallException);

		// test
		mockMvc.perform(post(String.format("/conversions"))
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(conversionPerformRequest)))
				.andExpect(status().isInternalServerError())
				.andExpect(content().string(containsString(String.valueOf(conversionApiCallException.getCode()))))
				.andExpect(content().string(containsString(conversionApiCallException.getMessage())));
	}

	private ConversionGetResponse createConversionGetResponse(Conversion conversion) {
		return ConversionGetResponse.builder()
				.amount(conversion.getAmount())
				.transactionId(conversion.getTransactionId())
				.build();
	}

	private List<ConversionsGetResponse> createConversionGetResponse(List<Conversion> conversionList) {
		ArrayList<ConversionsGetResponse> list = new ArrayList<>();
		conversionList.forEach(conversion -> list.add(ConversionsGetResponse.builder().conversionDate(new Date())
				.sourceCurrency("TRY")
				.targetCurrency("USD")
				.amount(new BigDecimal(100))
				.createdDate(new Date())
				.transactionId(conversion.getTransactionId())
				.build()));
		return list;
	}

	private Conversion createConversionObject() {
		return Conversion.builder().conversionDate(new Date())
				.sourceCurrency("TRY")
				.targetCurrency("USD")
				.amount(new BigDecimal(100))
				.createdDate(new Date())
				.lastModifiedDate(new Date())
				.transactionId(UUID.randomUUID())
				.build();
	}

	private ConversionPerformRequest createConversionPerformRequest(Conversion conversion) {
		return ConversionPerformRequest.builder().targetCurrency(conversion.getTargetCurrency())
				.sourceCurrency(conversion.getSourceCurrency())
				.sourceAmount(new BigDecimal(100))
				.build();
	}

	private ConversionPerformResponse createConversionPerformResponse(Conversion conversion) {
		return ConversionPerformResponse.builder().amount(conversion.getAmount())
				.transactionId(conversion.getTransactionId())
				.build();
	}
}