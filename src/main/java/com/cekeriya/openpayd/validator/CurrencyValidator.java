package com.cekeriya.openpayd.validator;

import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Currency;

public class CurrencyValidator implements ConstraintValidator<CurrencyCode, String> {
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (!StringUtils.hasText(value)) {
			return false;
		}

		try {
			Currency.getInstance(value);
		} catch (Exception e) {
			return false;
		}

		return true;
	}
}