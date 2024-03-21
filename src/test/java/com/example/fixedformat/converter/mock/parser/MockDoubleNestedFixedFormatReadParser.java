package com.example.fixedformat.converter.mock.parser;

import com.example.fixedformat.converter.mock.fixedformat.MockDoubleNestedFixedFormat;
import com.example.fixedformat.converter.mock.fixedformat.MockInnerFixedFormat;
import com.example.fixedformat.converter.mock.fixedformat.MockOuterFixedFormat;
import com.example.fixedformat.converter.mock.fixedformatrecord.*;
import com.example.fixedformat.parser.FixedFormatReadParser;
import com.example.fixedformat.parser.RecordType;

import java.util.List;

public class MockDoubleNestedFixedFormatReadParser extends FixedFormatReadParser {

    public MockDoubleNestedFixedFormatReadParser() {
        recordTypes.addAll(
                List.of(
                        new RecordType(MockFileHeadRecord::isValid, "File Head", MockFileHeadRecord::new, true, MockDoubleNestedFixedFormat::new),
                        new RecordType(MockHeadRecord::isValid, "Head", MockHeadRecord::new, true, MockOuterFixedFormat::new),
                        new RecordType(MockDetailRecord::isValid, "Detail", MockDetailRecord::new, true, MockInnerFixedFormat::new),
                        new RecordType(MockImportAmountRecord::isValid, "수입 금액", MockImportAmountRecord::new),
                        new RecordType(MockTailRecord::isValid, "Tail", MockTailRecord::new)
                )
        );
    }
}
