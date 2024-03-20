package com.example.fixedformat.formatter;

import com.example.fixedformat.exception.FixedFormatConverterException;

import java.lang.reflect.Field;

public interface FixedFormatFormatter {

    String pad(String value, Field field, int length) throws FixedFormatConverterException;

    String strip(String value, Field field) throws FixedFormatConverterException;
}
