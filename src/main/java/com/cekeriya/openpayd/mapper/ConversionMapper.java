package com.cekeriya.openpayd.mapper;

import com.cekeriya.openpayd.model.Conversion;
import com.cekeriya.openpayd.response.conversion.ConversionGetResponse;
import com.cekeriya.openpayd.response.conversion.ConversionPerformResponse;
import com.cekeriya.openpayd.response.conversion.ConversionsGetResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ConversionMapper {
	ConversionGetResponse toConversionResponse(Conversion conversion);

	List<ConversionsGetResponse> toConversionResponse(List<Conversion> conversions);

	ConversionPerformResponse toConversionPerformResponse(Conversion conversion);
}