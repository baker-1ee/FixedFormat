package com.example.fixedformat.converter.mock.fixedformat;

import com.example.fixedformat.common.FixedFormat;
import com.example.fixedformat.converter.mock.datajson.MockVatDataJson;
import com.example.fixedformat.converter.mock.fixedformatrecord.MockDetailRecord;
import com.example.fixedformat.converter.mock.fixedformatrecord.MockImportAmountRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MockInnerFixedFormat implements FixedFormat {

    private MockDetailRecord detailRecord;

    private List<MockImportAmountRecord> importAmountRecords;

    public static List<MockInnerFixedFormat> of(MockVatDataJson dataJson) {
        return List.of(
                MockInnerFixedFormat.builder()
                        .detailRecord(MockDetailRecord.of(dataJson))
                        .importAmountRecords(MockImportAmountRecord.of(dataJson))
                        .build(),
                MockInnerFixedFormat.builder()
                        .detailRecord(MockDetailRecord.of(dataJson))
                        .importAmountRecords(MockImportAmountRecord.of(dataJson))
                        .build()

        );
    }
}
