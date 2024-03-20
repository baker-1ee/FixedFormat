package com.example.fixedformat.converter.mock.fixedformat;

import com.example.fixedformat.common.FixedFormat;
import com.example.fixedformat.converter.mock.datajson.MockVatDataJson;
import com.example.fixedformat.converter.mock.fixedformatrecord.MockDetailRecord;
import com.example.fixedformat.converter.mock.fixedformatrecord.MockHeadRecord;
import com.example.fixedformat.converter.mock.fixedformatrecord.MockImportAmountRecord;
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
public class MockVatFixedFormat implements FixedFormat {

    private MockHeadRecord headRecord;

    private MockDetailRecord detailRecord;

    private List<MockImportAmountRecord> importAmountRecords;

    private MockTailRecord tailRecord;

    public static MockVatFixedFormat of(MockVatDataJson dataJson) {
        return MockVatFixedFormat.builder()
                .headRecord(MockHeadRecord.of(dataJson))
                .detailRecord(MockDetailRecord.of(dataJson))
                .importAmountRecords(MockImportAmountRecord.of(dataJson))
                .tailRecord(MockTailRecord.of(dataJson))
                .build();
    }

}
