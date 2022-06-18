package com.cekeriya.openpayd.service;

import com.cekeriya.openpayd.exception.ConversionApiCallException;
import com.cekeriya.openpayd.exception.NotFoundException;
import com.cekeriya.openpayd.model.Conversion;
import com.cekeriya.openpayd.repository.ConversionRepository;
import com.cekeriya.openpayd.request.ConversionPerformRequest;
import com.cekeriya.openpayd.response.exchange.ConvertResponse;
import com.cekeriya.openpayd.service.provider.ExchangeServiceProvider;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.cekeriya.openpayd.constant.ErrorType.CONVERSION_API_CALL_ERROR;
import static com.cekeriya.openpayd.constant.ErrorType.CONVERSION_NOT_FOUND;

@Service
public class ConversionService {
	private final ConversionRepository conversionRepository;

	private final ExchangeServiceProvider fixerService;

	public ConversionService(ConversionRepository conversionRepository, ExchangeServiceProvider fixerService) {
		this.conversionRepository = conversionRepository;
		this.fixerService = fixerService;
	}

	public List<Conversion> findByConversionDate(Date conversionDate, int page, int size) {
		PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdDate"));

		return conversionRepository.findByCreatedDateGreaterThan(conversionDate, pageRequest);
	}

	public Conversion findByTransactionId(UUID transactionId) {
		Optional<Conversion> opt = conversionRepository.findByTransactionId(transactionId);

		if (opt.isPresent()) {
			return opt.get();
		} else {
			throw new NotFoundException(CONVERSION_NOT_FOUND);
		}
	}

	public Conversion save(Conversion conversion) {
		return conversionRepository.save(conversion);
	}

	public Conversion performConversion(ConversionPerformRequest conversionPerformRequest) {
		String sourceCurrency = conversionPerformRequest.getSourceCurrency();
		String targetCurrency = conversionPerformRequest.getTargetCurrency();

		// get currency rate
		ConvertResponse convertResponse = fixerService.convert(sourceCurrency, targetCurrency, conversionPerformRequest.getSourceAmount());

		if (!convertResponse.isSuccess()) {
			throw new ConversionApiCallException(CONVERSION_API_CALL_ERROR);
		}

		// generate conversion
		Conversion conversion = Conversion.builder().amount(convertResponse.getResult())
				.sourceCurrency(sourceCurrency)
				.targetCurrency(targetCurrency)
				.conversionDate(convertResponse.getDate())
				.build();

		// save conversion
		Conversion savedConversion = save(conversion);

		return savedConversion;
	}
}
