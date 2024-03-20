package com.example.fixedformat.converter.mock.fixedformat;

import com.example.fixedformat.common.FixedFormat;
import com.example.fixedformat.converter.mock.datajson.MockVatDataJson;
import com.example.fixedformat.converter.mock.fixedformatrecord.MockHeadRecord;
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
public class MockNestedFixedFormat implements FixedFormat {

    private MockHeadRecord headRecord;

    private List<MockInnerFixedFormat> nestedRecord;

    private MockTailRecord tailRecord;

    public static MockNestedFixedFormat of(MockVatDataJson dataJson) {
        return MockNestedFixedFormat.builder()
                .headRecord(MockHeadRecord.of(dataJson))
                .nestedRecord(MockInnerFixedFormat.of(dataJson))
                .tailRecord(MockTailRecord.of(dataJson))
                .build();
    }

}
