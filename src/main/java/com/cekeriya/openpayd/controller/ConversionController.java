package com.cekeriya.openpayd.controller;

import com.cekeriya.openpayd.mapper.ConversionMapper;
import com.cekeriya.openpayd.request.ConversionPerformRequest;
import com.cekeriya.openpayd.response.error.ErrorResponse;
import com.cekeriya.openpayd.service.ConversionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.*;

import static com.cekeriya.openpayd.constant.ErrorType.MISSING_PARAMETER;

@Validated
@RestController
@RequestMapping("/conversions")
public class ConversionController {

	private final ConversionService conversionService;

	private final ConversionMapper mapper;

	public ConversionController(ConversionService conversionService, ConversionMapper mapper) {
		this.conversionService = conversionService;
		this.mapper = mapper;
	}

	@GetMapping("/{transactionId}")
	public ResponseEntity<?> getConversion(@PathVariable(name = "transactionId") UUID transactionId) {
		return new ResponseEntity(mapper.toConversionResponse(conversionService.findByTransactionId(transactionId)), HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity<?> getConversions(@RequestParam(name = "transactionId", required = false) UUID transactionId,
											@RequestParam(name = "conversionDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date transactionDate,
											@RequestParam(value = "page", defaultValue = "0") @PositiveOrZero int page,
											@RequestParam(value = "size", defaultValue = "25") @Positive int size) {
		// PS: searching with transactionId logic should extract from this api. This api should support only for transactionDate with pagination
		if (Objects.isNull(transactionId) && Objects.isNull(transactionDate)) {
			return new ResponseEntity(new ErrorResponse(HttpStatus.BAD_REQUEST,
					MISSING_PARAMETER.getCode(), "Transaction id or transaction date must be provided"), HttpStatus.BAD_REQUEST);
		}

		if (Objects.nonNull(transactionId)) {
			return new ResponseEntity(Collections.singletonList(mapper.toConversionResponse(conversionService.findByTransactionId(transactionId))), HttpStatus.OK);

		} else {
			return new ResponseEntity<>(mapper.toConversionResponse(conversionService.findByConversionDate(transactionDate, page, size)), HttpStatus.OK);
		}
	}

	@PostMapping()
	public ResponseEntity<?> performConversions(@RequestBody @Valid ConversionPerformRequest conversionPerformRequest) {
		return new ResponseEntity<>(mapper.toConversionPerformResponse(conversionService.performConversion(conversionPerformRequest)), HttpStatus.OK);
	}

}
