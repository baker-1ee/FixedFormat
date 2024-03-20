package com.example.fixedformat.formatter;

import com.example.fixedformat.aop.FixedFormatColumn;
import com.example.fixedformat.common.CommonConstants;
import com.example.fixedformat.exception.FixedFormatConverterException;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;

import static org.apache.commons.lang.StringUtils.*;

public class FixedFormatDefaultFormatter implements FixedFormatFormatter {

    @Override
    public String pad(String value, Field field, int length) throws FixedFormatConverterException {
        String safeValue = substringByBytes(value.trim(), 0, field.getAnnotation(FixedFormatColumn.class).size(), CommonConstants.EUC_KR);
        if (field.getType().equals(String.class)) {
            return rightPad(safeValue, length, " ");
        } else if (field.getType().equals(Long.class)) {
            if (isEmpty(safeValue)) {
                safeValue = "0";
            }
            return leftPad(safeValue, length, "0");
        } else {
            throw new FixedFormatConverterException("FixedFormatDefaultFormatter pad exception");
        }
    }

    @Override
    public String strip(String value, Field field) throws FixedFormatConverterException {
        if (field.getType().equals(String.class)) {
            return stripEnd(value, " ");
        } else if (field.getType().equals(Long.class)) {
            return stripStart(value, "0");
        } else {
            throw new FixedFormatConverterException("FixedFormatDefaultFormatter strip exception");
        }
    }

    private String substringByBytes(String str, int beginBytes, int endBytes, String characterSet) throws FixedFormatConverterException {
        if (str == null || str.length() == 0) {
            return "";
        }

        if (beginBytes < 0) {
            beginBytes = 0;
        }

        if (endBytes < 1) {
            return "";
        }

        int len = str.length();

        int beginIndex = -1;
        int endIndex = 0;

        int curBytes = 0;
        String ch;
        for (int i = 0; i < len; i++) {
            ch = str.substring(i, i + 1);
            try {
                curBytes += ch.getBytes(characterSet).length;
            } catch (UnsupportedEncodingException e) {
                throw new FixedFormatConverterException("FixedFormatConverter Exception - substringByBytes method");
            }

            if (beginIndex == -1 && curBytes >= beginBytes) {
                beginIndex = i;
            }

            if (curBytes > endBytes) {
                break;
            } else {
                endIndex = i + 1;
            }
        }

        return str.substring(beginIndex, endIndex);
    }
}
