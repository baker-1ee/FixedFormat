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
public class MockDetailRecord implements FixedFormatRecord {

    @FixedFormatColumn(size = 2, nullable = false)
    private String dataDivision;

    @FixedFormatColumn(size = 7, nullable = false)
    private String formattingCode;

    @FixedFormatColumn(size = 15, nullable = false)
    private Long salesTaxInvoiceAmount;

    @FixedFormatColumn(size = 13, nullable = false)
    private Long salesTaxInvoiceTaxAmount;

    public static MockDetailRecord of(MockVatDataJson dataJson) {
        return MockDetailRecord.builder()
                .dataDivision("17")
                .formattingCode("I103200")
                .salesTaxInvoiceAmount(dataJson.getSalesTaxInvoiceAmount())
                .salesTaxInvoiceTaxAmount(dataJson.getSalesTaxInvoiceTaxAmount())
                .build();
    }

    public static boolean isValid(String line, byte[] lineBytes) {
        return line.startsWith("17I103200") && lineBytes.length == 37;
    }
}
