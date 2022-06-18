package com.cekeriya.openpayd.controller;

import com.cekeriya.openpayd.mapper.ConversionMapper;
import com.cekeriya.openpayd.request.ConversionPerformRequest;
import com.cekeriya.openpayd.response.conversion.ConversionGetResponse;
import com.cekeriya.openpayd.response.conversion.ConversionPerformResponse;
import com.cekeriya.openpayd.response.conversion.ConversionsGetResponse;
import com.cekeriya.openpayd.response.error.ErrorResponse;
import com.cekeriya.openpayd.service.ConversionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

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

	@Operation(summary = "Get conversion details")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successful Response", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ConversionGetResponse.class))}),
			@ApiResponse(responseCode = "400", description = "Bad Request - If given transaction id is not valid, returns INVALID_PARAMETER(10001) code", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
			@ApiResponse(responseCode = "404", description = "Not Found - If any conversion can not be found according to transaction id, returns CONVERSION_NOT_FOUND(10002) code", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})})
	@GetMapping("/{transactionId}")
	public ResponseEntity<ConversionGetResponse> getConversion(@PathVariable(name = "transactionId") UUID transactionId) {
		return new ResponseEntity(mapper.toConversionResponse(conversionService.findByTransactionId(transactionId)), HttpStatus.OK);
	}

	@Operation(summary = "Get conversion details according to transaction id or transaction date")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successful Response", content = {
					@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ConversionsGetResponse.class)))}),
			@ApiResponse(responseCode = "400", description = "Bad Request - If any of given transaction id, transaction date, page or size parameters are not valid, " +
																	 "returns INVALID_PARAMETER(10001) code", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
			@ApiResponse(responseCode = "404", description = "Not Found - If any conversion can not be found according to transaction id, returns CONVERSION_NOT_FOUND(10002) code", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})})
	@GetMapping
	public ResponseEntity<?> getConversions(@RequestParam(name = "transactionId", required = false) UUID transactionId,
											@RequestParam(name = "conversionDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @PastOrPresent Date transactionDate,
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
			return new ResponseEntity(mapper.toConversionResponse(conversionService.findByConversionDate(transactionDate, page, size)), HttpStatus.OK);
		}
	}

	@Operation(summary = "Perform exchange according to given parameters")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successful Response", content = {
					@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ConversionPerformResponse.class)))}),
			@ApiResponse(responseCode = "400", description = "Bad Request - If any of given source and target currencies or exchange amount are not valid, " +
																	 "returns INVALID_PARAMETER(10001) code", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
			@ApiResponse(responseCode = "500", description = "Interval Server Error - If any problem occurred during service provider access, returns CONVERSION_API_CALL_ERROR(10003) code", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})})
	@PostMapping()
	public ResponseEntity<ConversionPerformResponse> performConversions(@RequestBody @Valid ConversionPerformRequest conversionPerformRequest) {
		return new ResponseEntity<>(mapper.toConversionPerformResponse(conversionService.performConversion(conversionPerformRequest)), HttpStatus.OK);
	}

}
