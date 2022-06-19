package com.cekeriya.openpayd.service;

import com.cekeriya.openpayd.constant.ErrorType;
import com.cekeriya.openpayd.exception.ConversionApiCallException;
import com.cekeriya.openpayd.exception.NotFoundException;
import com.cekeriya.openpayd.model.Conversion;
import com.cekeriya.openpayd.repository.ConversionRepository;
import com.cekeriya.openpayd.request.ConversionPerformRequest;
import com.cekeriya.openpayd.response.exchange.ConvertResponse;
import com.cekeriya.openpayd.service.provider.ExchangeServiceProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest
public class ConversionServiceTest {
	@Autowired
	private ConversionService conversionService;

	@MockBean
	private ConversionRepository mockConversionRepository;

	@MockBean
	private ExchangeServiceProvider mockExchangeServiceProvider;


	@BeforeEach
	void setUp() {
	}

	@AfterEach
	void tearDown() {
	}

	@Test
	void save_success() {
		// prepare
		Conversion conversion = createConversionObject();

		// mock
		Mockito.when(mockConversionRepository.save(conversion)).thenReturn(conversion);

		// operation
		Conversion savedConversion = conversionService.save(conversion);

		// test
		assertSame(savedConversion, conversion);
	}

	@Test
	void findByTransactionId_success() {
		// prepare
		Conversion conversion = createConversionObject();

		// mock
		Mockito.when(mockConversionRepository.findByTransactionId(any(UUID.class))).thenReturn(Optional.of(conversion));

		// operation
		Conversion findConversion = conversionService.findByTransactionId(conversion.getTransactionId());

		// test
		assertSame(findConversion, conversion);
	}

	@Test
	void findByTransactionId_notFoundException() {
		// mock
		Mockito.when(mockConversionRepository.findByTransactionId(any(UUID.class))).thenReturn(Optional.empty());

		// operation
		NotFoundException exception = assertThrows(NotFoundException.class, () -> {
			conversionService.findByTransactionId(UUID.randomUUID());
		});

		// test
		assertSame(ErrorType.CONVERSION_NOT_FOUND.getMessage(), exception.getMessage());
		assertTrue(ErrorType.CONVERSION_NOT_FOUND.getCode() == exception.getCode());
	}

	@Test
	void findByConversionDate_success() {
		// prepare
		Conversion conversion = createConversionObject();
		List<Conversion> conversionList = Arrays.asList(conversion);

		// mock
		Mockito.when(mockConversionRepository.findByConversionDateLessThan(any(Date.class), any(Pageable.class))).thenReturn(conversionList);

		// operation
		List<Conversion> resultConversionList = conversionService.findByConversionDate(new Date(), 1, 2);

		// test
		assertSame(resultConversionList, conversionList);
	}

	@Test
	void findByConversionDate_emptyResult() {
		// mock
		Mockito.when(mockConversionRepository.findByConversionDateLessThan(any(Date.class), any(Pageable.class))).thenReturn(Collections.EMPTY_LIST);

		// operation
		List<Conversion> resultConversionList = conversionService.findByConversionDate(new Date(), 1, 2);

		// test
		assertTrue(resultConversionList.isEmpty());
	}

	@Test
	void performConversion_success() {
		// prepare
		Conversion conversion = createConversionObject();
		ConversionPerformRequest conversionPerformRequest = createConversionPerformRequest(conversion);
		ConvertResponse convertResponse = createConvertResponse(conversion);

		// mock
		Mockito.when(mockConversionRepository.save(any(Conversion.class))).thenReturn(conversion);
		Mockito.when(mockExchangeServiceProvider.convert(anyString(), anyString(), any(BigDecimal.class))).thenReturn(convertResponse);

		// operation
		Conversion resultConversion = conversionService.performConversion(conversionPerformRequest);

		// test
		assertSame(resultConversion, conversion);
	}

	@Test
	void performConversion_conversionApiCallException() {
		// prepare
		Conversion conversion = createConversionObject();
		ConversionPerformRequest conversionPerformRequest = createConversionPerformRequest(conversion);
		ConvertResponse convertResponse = createConvertResponse(conversion);
		convertResponse.setSuccess(false);

		// mock
		Mockito.when(mockExchangeServiceProvider.convert(anyString(), anyString(), any(BigDecimal.class))).thenReturn(convertResponse);

		// operation
		ConversionApiCallException exception = assertThrows(ConversionApiCallException.class, () -> {
			conversionService.performConversion(conversionPerformRequest);
		});

		// test
		assertSame(ErrorType.CONVERSION_API_CALL_ERROR.getMessage(), exception.getMessage());
		assertTrue(ErrorType.CONVERSION_API_CALL_ERROR.getCode() == exception.getCode());
	}


	private Conversion createConversionObject() {
		return Conversion.builder()
				.conversionDate(new Date())
				.sourceCurrency("TRY")
				.targetCurrency("USD")
				.amount(new BigDecimal(100))
				.createdDate(new Date())
				.lastModifiedDate(new Date())
				.transactionId(UUID.randomUUID())
				.build();
	}

	private ConversionPerformRequest createConversionPerformRequest(Conversion conversion) {
		return ConversionPerformRequest.builder()
				.sourceAmount(new BigDecimal(100))
				.sourceCurrency(conversion.getSourceCurrency())
				.targetCurrency(conversion.getTargetCurrency())
				.build();
	}

	private ConvertResponse createConvertResponse(Conversion conversion) {
		return ConvertResponse.builder()
				.result(conversion.getAmount())
				.date(conversion.getConversionDate())
				.success(true)
				.build();
	}
}
