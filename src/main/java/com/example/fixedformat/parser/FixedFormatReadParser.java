package com.example.fixedformat.parser;

import com.example.fixedformat.exception.FixedFormatConverterException;

import java.util.ArrayList;
import java.util.List;

public abstract class FixedFormatReadParser {

    protected List<RecordType> recordTypes = new ArrayList<>();

    public RecordType findMatchedRecord(String line, byte[] lineBytes) throws FixedFormatConverterException {
        return recordTypes.stream()
                .filter(i -> i.getValidPredicate().test(line, lineBytes))
                .findFirst()
                .orElseThrow(() -> new FixedFormatConverterException("FixedFormatReadParser exception"));
    }
}
