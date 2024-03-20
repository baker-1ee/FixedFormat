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
public class MockTailRecord implements FixedFormatRecord {

    @FixedFormatColumn(size = 2, nullable = false)
    private String dataDivision;

    @FixedFormatColumn(size = 7, nullable = false)
    private String formattingCode;

    @FixedFormatColumn(size = 3, nullable = false)
    private String deductionReductionCode;

    @FixedFormatColumn(size = 15, nullable = false)
    private Long deductionReductionAmount;

    public static MockTailRecord of(MockVatDataJson mock) {
        return MockTailRecord.builder()
                .dataDivision("14")
                .formattingCode("I103200")
                .deductionReductionCode(mock.getDeductionReductionCode())
                .deductionReductionAmount(mock.getDeductionReductionAmount())
                .build();
    }

    public static boolean isValid(String line, byte[] lineBytes) {
        return line.startsWith("14I103200") && lineBytes.length == 27;
    }
}
