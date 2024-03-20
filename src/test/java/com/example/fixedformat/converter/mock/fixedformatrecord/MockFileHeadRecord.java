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
public class MockFileHeadRecord implements FixedFormatRecord {

    @FixedFormatColumn(size = 2, nullable = false)
    private String dataDivision;

    @FixedFormatColumn(size = 15, nullable = false)
    private String fileName;

    public static MockFileHeadRecord of(MockVatDataJson dataJson) {
        return MockFileHeadRecord.builder()
                .dataDivision("10")
                .fileName("File Start")
                .build();
    }

    public static boolean isValid(String line, byte[] lineBytes) {
        return line.startsWith("10") && lineBytes.length == 17;
    }

}
