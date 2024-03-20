package com.example.fixedformat.converter.mock.fixedformat;

import com.example.fixedformat.common.FixedFormat;
import com.example.fixedformat.converter.mock.datajson.MockVatDataJson;
import com.example.fixedformat.converter.mock.fixedformatrecord.MockFileHeadRecord;
import com.example.fixedformat.converter.mock.fixedformatrecord.MockTailRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MockDoubleNestedFixedFormat implements FixedFormat {

    private MockFileHeadRecord fileHeadRecord;

    private List<MockOuterFixedFormat> outerFixedFormats;

    private MockTailRecord tailRecord;

    public static MockDoubleNestedFixedFormat of(MockVatDataJson mockVatDataJson) {
        return MockDoubleNestedFixedFormat.builder()
                .fileHeadRecord(MockFileHeadRecord.of(mockVatDataJson))
                .outerFixedFormats(MockOuterFixedFormat.of(mockVatDataJson))
                .tailRecord(MockTailRecord.of(mockVatDataJson))
                .build();
    }
}
