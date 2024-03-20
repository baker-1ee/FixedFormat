package com.example.fixedformat.parser;

import com.example.fixedformat.common.FixedFormat;
import com.example.fixedformat.common.FixedFormatRecord;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.BiPredicate;
import java.util.function.Supplier;

@AllArgsConstructor
@Getter
public class RecordType {

    private final BiPredicate<String, byte[]> validPredicate;
    private final String name;
    private final Supplier<FixedFormatRecord> fixedFormatRecordGenerator;
    private final boolean isHeaderRecord;
    private final Supplier<FixedFormat> fixedFormatGenerator;

    public RecordType(BiPredicate<String, byte[]> validPredicate, String name, Supplier<FixedFormatRecord> fixedFormatRecordGenerator) {
        this.validPredicate = validPredicate;
        this.name = name;
        this.fixedFormatRecordGenerator = fixedFormatRecordGenerator;
        this.isHeaderRecord = false;
        this.fixedFormatGenerator = null;
    }
}
