package com.example.fixedformat.converter;

import com.example.fixedformat.common.FixedFormat;
import com.example.fixedformat.common.FixedFormatRecord;
import lombok.Builder;
import lombok.Getter;

import java.util.function.Supplier;

@Getter
@Builder
public class Record {
    
    private boolean isHeaderRecord;

    private FixedFormatRecord fixedFormatRecord;

    private Supplier<FixedFormat> fixedFormatGenerator;

}
