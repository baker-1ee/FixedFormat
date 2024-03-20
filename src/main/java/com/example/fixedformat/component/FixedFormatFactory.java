package com.example.fixedformat.component;

import com.example.fixedformat.common.CommonConstants;
import com.example.fixedformat.common.FixedFormat;
import com.example.fixedformat.converter.FixedFormatConverter;
import com.example.fixedformat.formatter.FixedFormatDefaultFormatter;
import com.example.fixedformat.formatter.FixedFormatFormatter;
import com.example.fixedformat.parser.FixedFormatReadParser;
import org.springframework.stereotype.Component;

@Component
public class FixedFormatFactory {

    private final FixedFormatFormatter defaultFormatter = new FixedFormatDefaultFormatter();
    private final String defaultLineSeparator = CommonConstants.CRLF;
    private final String defaultCharacterSet = CommonConstants.EUC_KR;

    public <T extends FixedFormat> FixedFormatConverter<T> getConverter(FixedFormatReadParser parser) {
        return new FixedFormatConverter<T>(defaultLineSeparator, defaultCharacterSet, defaultFormatter, parser);
    }
}
