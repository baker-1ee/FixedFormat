package com.example.fixedformat.converter.mock.fixedformatrecord;

import com.example.fixedformat.aop.FixedFormatColumn;
import com.example.fixedformat.common.FixedFormatRecord;
import com.example.fixedformat.converter.mock.datajson.MockVatDataJson;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MockImportAmountRecord implements FixedFormatRecord {

    @FixedFormatColumn(size = 2, nullable = false)
    private String dataDivision;

    @FixedFormatColumn(size = 7, nullable = false)
    private String formattingCode;

    @FixedFormatColumn(size = 2, nullable = false)
    private String importAmountDivisionCode;

    @FixedFormatColumn(size = 15, nullable = false)
    private Long importAmount;

    public static List<MockImportAmountRecord> of(MockVatDataJson mock) {
        if (mock.getImportAmounts() == null) return null;
        return mock.getImportAmounts().stream()
                .map(e -> MockImportAmountRecord.builder()
                        .dataDivision("15")
                        .formattingCode("I103200")
                        .importAmountDivisionCode(e.getImportAmountDivisionCode())
                        .importAmount(e.getImportAmount())
                        .build())
                .toList();
    }

    public static boolean isValid(String line, byte[] lineBytes) {
        return line.startsWith("15I103200") && lineBytes.length == 26;
    }
}
