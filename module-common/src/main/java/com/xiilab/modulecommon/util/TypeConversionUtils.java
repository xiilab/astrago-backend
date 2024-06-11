package com.xiilab.modulecommon.util;

import java.math.BigDecimal;
import java.util.Objects;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TypeConversionUtils {
	/**
	 * Number -> Float 변환 메서드
	 *
	 * @param number
	 * @return
	 */
	public Float toFloat(Number number) {
		return !Objects.isNull(number)? number.floatValue() : null;
	}

	/**
	 * Number -> BigDecimal 변환 메서드
	 *
	 * @param number
	 * @return
	 */
	public BigDecimal toBigDecimal(Float number) {
		return !Objects.isNull(number)? BigDecimal.valueOf(number) : null;
	}
}
