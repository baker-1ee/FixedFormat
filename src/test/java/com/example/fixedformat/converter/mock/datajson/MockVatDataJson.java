package com.example.fixedformat.converter.mock.datajson;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MockVatDataJson {

    private String taxMasterName;

    private Long salesTaxInvoiceAmount;

    private Long salesTaxInvoiceTaxAmount;

    private List<MockImportAmount> importAmounts;

    private String deductionReductionCode;

    private Long deductionReductionAmount;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MockImportAmount {

        private String importAmountDivisionCode;

        private Long importAmount;
    }
}
