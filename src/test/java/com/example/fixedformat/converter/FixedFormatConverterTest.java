package com.example.fixedformat.converter;

import com.example.fixedformat.component.FixedFormatFactory;
import com.example.fixedformat.converter.mock.FixedFormatHomeTaxFormatter;
import com.example.fixedformat.converter.mock.fixedformat.MockDoubleNestedFixedFormat;
import com.example.fixedformat.converter.mock.fixedformat.MockNestedFixedFormat;
import com.example.fixedformat.converter.mock.fixedformat.MockVatFixedFormat;
import com.example.fixedformat.converter.mock.parser.MockFixedFormatReadParser;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest(classes = FixedFormatFactory.class)
class FixedFormatConverterTest {

    @Autowired
    FixedFormatFactory fixedFormatFactory;

    FixedFormatConverter<MockVatFixedFormat> fixedFormatConverter;
    FixedFormatConverter<MockNestedFixedFormat> nestedFixedFormatConverter;
    FixedFormatConverter<MockDoubleNestedFixedFormat> doubleNestedFixedFormatConverter;

    @PostConstruct
    void init() {
        fixedFormatConverter = fixedFormatFactory.getConverter(new FixedFormatHomeTaxFormatter(), new MockFixedFormatReadParser());
    }

}
