package com.example.fixedformat.converter.mock.fixedformatrecord;

import com.example.fixedformat.aop.FixedFormatColumn;
import com.example.fixedformat.common.FixedFormatRecord;
import com.example.fixedformat.converter.mock.datajson.MockVatDataJson;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MockHeadRecord implements FixedFormatRecord {

    public static final int LENGTH = 22;
    public static final String DATA_DIVISION = "11I103200";

    @FixedFormatColumn(size = 2, nullable = false)
    private String dataDivision;

    @FixedFormatColumn(size = 7, nullable = false)
    private String formattingCode;

    @FixedFormatColumn(size = 10, nullable = false)
    private String taxMasterName;

    @FixedFormatColumn(size = 3)
    private String blankSpace;

    public static MockHeadRecord of(MockVatDataJson dataJson) {
        return MockHeadRecord.builder()
                .dataDivision("11")
                .formattingCode("I103200")
                .taxMasterName(dataJson.getTaxMasterName())
                .blankSpace("")
                .build();
    }

    public static boolean isValid(String line, byte[] lineBytes) {
        return line.startsWith(DATA_DIVISION) && lineBytes.length == LENGTH;
    }

}
