package com.example.fixedformat.converter.mock;

import com.example.fixedformat.aop.FixedFormatColumn;
import com.example.fixedformat.common.CommonConstants;
import com.example.fixedformat.exception.FixedFormatConverterException;
import com.example.fixedformat.formatter.FixedFormatDefaultFormatter;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Field;

public class FixedFormatHomeTaxFormatter extends FixedFormatDefaultFormatter {

    @Override
    public String pad(String value, Field field, int length) throws FixedFormatConverterException {
        String safeValue = substringByBytes(value.trim(), 0, field.getAnnotation(FixedFormatColumn.class).size(), CommonConstants.EUC_KR);

        if (field.getType().equals(String.class)) {
            return StringUtils.rightPad(safeValue, length, " ");
        } else if (field.getType().equals(Long.class)) {
            if (StringUtils.isEmpty(safeValue)) safeValue = "0";
            long number = Long.parseLong(safeValue);
            if (number < 0) {
                return "-" + StringUtils.leftPad(String.valueOf(number * -1), length - 1, "0");
            } else {
                return StringUtils.leftPad(safeValue, length, "0");
            }
        } else if (field.getType().equals(Double.class)) {
            if (StringUtils.isEmpty(safeValue)) safeValue = "0";
            double number = Double.parseDouble(safeValue);
            if (number < 0) {
                return "-" + StringUtils.leftPad(String.valueOf(number * -1), length - 1, "0");
            } else {
                return StringUtils.leftPad(safeValue, length, "0");
            }
        } else {
            throw new FixedFormatConverterException("FixedFormatHomeTaxFormatter pad exception");
        }
    }
}
