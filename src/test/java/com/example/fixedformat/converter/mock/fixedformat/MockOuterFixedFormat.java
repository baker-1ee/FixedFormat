package com.example.fixedformat.converter.mock.fixedformat;

import com.example.fixedformat.common.FixedFormat;
import com.example.fixedformat.converter.mock.datajson.MockVatDataJson;
import com.example.fixedformat.converter.mock.fixedformatrecord.MockHeadRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MockOuterFixedFormat implements FixedFormat {

    private MockHeadRecord headRecord;

    private List<MockInnerFixedFormat> nestedRecord;

    public static List<MockOuterFixedFormat> of(MockVatDataJson dataJson) {
        return List.of(MockOuterFixedFormat.builder()
                        .headRecord(MockHeadRecord.of(dataJson))
                        .nestedRecord(MockInnerFixedFormat.of(dataJson))
                        .build(),
                MockOuterFixedFormat.builder()
                        .headRecord(MockHeadRecord.of(dataJson))
                        .nestedRecord(MockInnerFixedFormat.of(dataJson))
                        .build()
        );
    }
}
