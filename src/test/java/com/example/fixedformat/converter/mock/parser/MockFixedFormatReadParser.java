package com.example.fixedformat.converter.mock.parser;

import com.example.fixedformat.converter.mock.fixedformat.MockVatFixedFormat;
import com.example.fixedformat.converter.mock.fixedformatrecord.MockDetailRecord;
import com.example.fixedformat.converter.mock.fixedformatrecord.MockHeadRecord;
import com.example.fixedformat.converter.mock.fixedformatrecord.MockImportAmountRecord;
import com.example.fixedformat.converter.mock.fixedformatrecord.MockTailRecord;
import com.example.fixedformat.parser.FixedFormatReadParser;
import com.example.fixedformat.parser.RecordType;

import java.util.List;

public class MockFixedFormatReadParser extends FixedFormatReadParser {

    public MockFixedFormatReadParser() {
        recordTypes.addAll(
                List.of(
                        new RecordType(MockHeadRecord::isValid, "신고서 Head", MockHeadRecord::new, true, MockVatFixedFormat::new),
                        new RecordType(MockDetailRecord::isValid, "일반 과세자 신고서", MockDetailRecord::new),
                        new RecordType(MockImportAmountRecord::isValid, "수입 금액 신고서", MockImportAmountRecord::new),
                        new RecordType(MockTailRecord::isValid, "공제 감면 신고서", MockTailRecord::new)
                )
        );
    }
}
