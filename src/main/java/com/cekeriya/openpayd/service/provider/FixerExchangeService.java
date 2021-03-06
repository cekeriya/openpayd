package com.cekeriya.openpayd.service.provider;

import com.cekeriya.openpayd.exception.ConversionApiCallException;
import com.cekeriya.openpayd.response.exchange.ConvertResponse;
import com.cekeriya.openpayd.response.exchange.RateResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Objects;

import static com.cekeriya.openpayd.constant.ErrorType.CONVERSION_API_CALL_ERROR;

@Slf4j
@Service
public class FixerExchangeService implements ExchangeServiceProvider {
	private static final String API_KEY = "apiKey";

	@Value("${fixer.service.provider.convert.url}")
	private String fixerServiceConvertUrl;

	@Value("${fixer.service.provider.rate.url}")
	private String fixerServiceRateUrl;

	@Value("${fixer.service.provider.access.key}")
	private String fixerServiceAccessKey;

	private final ObjectMapper objectMapper;

	private final OkHttpClient client;


	@Autowired
	public FixerExchangeService(ObjectMapper objectMapper) {
		this.client = new OkHttpClient().newBuilder().build();
		this.objectMapper = objectMapper;
	}

	/**
	 * Sends a request to fixer service provider and gets perform convert
	 *
	 * @param sourceCurrency source currency
	 * @param targetCurrency target currency
	 * @param amount amount
	 * @return convert result
	 */
	@Override
	public ConvertResponse convert(String sourceCurrency, String targetCurrency, BigDecimal amount) {
		// generate convert url with parameters
		String url = String.format(fixerServiceConvertUrl, sourceCurrency, targetCurrency, amount);

		// generate request
		Request request = new Request.Builder()
				.url(url)
				.addHeader(API_KEY, fixerServiceAccessKey)
				.get()
				.build();

		Response response;
		try {
			response = client.newCall(request).execute();
		} catch (IOException e) {
			log.error("[FIXER_SERVICE] [CONVERT] [API_CALL_EXCEPTION] [MESSAGE={}]", e.getMessage());
			throw new ConversionApiCallException(CONVERSION_API_CALL_ERROR);
		}

		if (response.isSuccessful()) {
			try {
				return objectMapper.readValue(Objects.requireNonNull(response.body()).string(), ConvertResponse.class);
			} catch (IOException e) {
				log.error("[FIXER_SERVICE] [CONVERT] [MAPPER_EXCEPTION] [MESSAGE={}]", e.getMessage());
				throw new ConversionApiCallException(CONVERSION_API_CALL_ERROR);
			}
		} else {
			log.error("[FIXER_SERVICE] [CONVERT] [UNSUCCESSFUL] [MESSAGE={}]", response.message());
			throw new ConversionApiCallException(CONVERSION_API_CALL_ERROR);
		}
	}

	/**
	 * Sends a request to fixer service provider and gets rates for specified currencies
	 *
	 * @param sourceCurrency source currency
	 * @param targetCurrency target currency
	 * @return currency rates
	 */
	@Override
	public RateResponse rate(String sourceCurrency, String targetCurrency) {
		// generate rate url with parameters
		String url = String.format(fixerServiceRateUrl, sourceCurrency, targetCurrency);

		// generate request
		Request request = new Request.Builder()
				.url(url)
				.addHeader(API_KEY, fixerServiceAccessKey)
				.get()
				.build();

		Response response;
		try {
			response = client.newCall(request).execute();
		} catch (IOException e) {
			log.error("[FIXER_SERVICE] [RATE] [API_CALL_EXCEPTION] [MESSAGE={}]", e.getMessage());
			throw new ConversionApiCallException(CONVERSION_API_CALL_ERROR);
		}

		if (response.isSuccessful()) {
			try {
				return objectMapper.readValue(Objects.requireNonNull(response.body()).string(), RateResponse.class);
			} catch (IOException e) {
				log.error("[FIXER_SERVICE] [RATE] [MAPPER_EXCEPTION] [MESSAGE={}]", e.getMessage());
				throw new ConversionApiCallException(CONVERSION_API_CALL_ERROR);
			}
		} else {
			log.error("[FIXER_SERVICE] [RATE] [UNSUCCESSFUL] [MESSAGE={}]", response.message());
			throw new ConversionApiCallException(CONVERSION_API_CALL_ERROR);
		}
	}
}

