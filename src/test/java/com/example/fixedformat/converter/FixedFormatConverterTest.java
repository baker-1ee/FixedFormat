package com.example.fixedformat.converter;

import com.example.fixedformat.common.CommonConstants;
import com.example.fixedformat.common.FixedFormat;
import com.example.fixedformat.component.FixedFormatFactory;
import com.example.fixedformat.converter.mock.FixedFormatHomeTaxFormatter;
import com.example.fixedformat.converter.mock.datajson.MockVatDataJson;
import com.example.fixedformat.converter.mock.fixedformat.MockDoubleNestedFixedFormat;
import com.example.fixedformat.converter.mock.fixedformat.MockNestedFixedFormat;
import com.example.fixedformat.converter.mock.fixedformat.MockVatFixedFormat;
import com.example.fixedformat.converter.mock.fixedformatrecord.MockDetailRecord;
import com.example.fixedformat.converter.mock.parser.MockDoubleNestedFixedFormatReadParser;
import com.example.fixedformat.converter.mock.parser.MockFixedFormatReadParser;
import com.example.fixedformat.converter.mock.parser.MockNestedFixedFormatReadParser;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(classes = FixedFormatFactory.class)
class FixedFormatConverterTest {

    @Autowired
    FixedFormatFactory fixedFormatFactory;

    FixedFormatConverter<MockVatFixedFormat> fixedFormatConverter;
    FixedFormatConverter<MockNestedFixedFormat> nestedFixedFormatConverter;
    FixedFormatConverter<MockDoubleNestedFixedFormat> doubleNestedFixedFormatConverter;

    @PostConstruct
    void init() {
        fixedFormatConverter = fixedFormatFactory.getConverter(new FixedFormatHomeTaxFormatter(), new MockFixedFormatReadParser());
        nestedFixedFormatConverter = fixedFormatFactory.getConverter(new FixedFormatHomeTaxFormatter(), new MockNestedFixedFormatReadParser());
        doubleNestedFixedFormatConverter = fixedFormatFactory.getConverter(new FixedFormatHomeTaxFormatter(), new MockDoubleNestedFixedFormatReadParser());
    }

    @Test
    @SneakyThrows
    void model_to_bytes_convert() {
        // given
        MockVatDataJson mockVatDataJson = loadMockDataJson();
        FixedFormat vatFixedFormat = MockVatFixedFormat.of(mockVatDataJson);

        // when
        byte[] bytes = fixedFormatConverter.convert(vatFixedFormat);

        // then
        String[] lines = new String(bytes).split(CommonConstants.CRLF);
        assertThat(lines.length).isEqualTo(5);
    }

    private MockVatDataJson loadMockDataJson() {
        return MockVatDataJson.builder()
                .taxMasterName("김세무")
                .salesTaxInvoiceAmount(1000000L)
                .salesTaxInvoiceTaxAmount(100000L)
                .importAmounts(List.of(
                                new MockVatDataJson.MockImportAmount("01", -5000L),
                                new MockVatDataJson.MockImportAmount("02", 4000L)
                        )
                )
                .deductionReductionCode("211")
                .deductionReductionAmount(3000L)
                .build();
    }

    @SneakyThrows
    @Test
    void models_to_bytes_convert() {

        // given
        List<FixedFormat> vatFixedFormats = List.of(MockVatFixedFormat.of(loadMockDataJson()), MockVatFixedFormat.of(loadMockDataJson2()));

        // when
        byte[] bytes = fixedFormatConverter.convert(vatFixedFormats);

        // then
        String[] lines = new String(bytes).split(CommonConstants.CRLF);
        assertThat(lines.length).isEqualTo(11);

    }

    private MockVatDataJson loadMockDataJson2() {
        return MockVatDataJson.builder()
                .taxMasterName("하도훈")
                .salesTaxInvoiceAmount(1000000L)
                .salesTaxInvoiceTaxAmount(100000L)
                .importAmounts(List.of(
                                new MockVatDataJson.MockImportAmount("01", 5000L),
                                new MockVatDataJson.MockImportAmount("02", 4000L),
                                new MockVatDataJson.MockImportAmount("03", 4000L)
                        )
                )
                .deductionReductionCode("211")
                .deductionReductionAmount(-3000L)
                .build();
    }

    @SneakyThrows
    @Test
    void bytes_to_models_convert() {
        // given
        List<FixedFormat> vatFixedFormats = List.of(MockVatFixedFormat.of(loadMockDataJson()), MockVatFixedFormat.of(loadMockDataJson2()));
        byte[] bytes = fixedFormatConverter.convert(vatFixedFormats);

        // when
        List<MockVatFixedFormat> models = fixedFormatConverter.convert(bytes);

        // then
        assertThat(models.size()).isEqualTo(2);
        for (MockVatFixedFormat actual : models) {
            assertThat(actual.getHeadRecord().getDataDivision()).isEqualTo("11");
            assertThat(actual.getHeadRecord().getFormattingCode()).isEqualTo("I103200");
        }
    }

    @SneakyThrows
    @Test
    @DisplayName("model to bytes convert 후 bytes to model convert 하면 instance의 convert 전과 후의 값이 동일해야한다.")
    void mustBeTheSameForTwoWayConversion() {
        // given
        List<FixedFormat> originModels = List.of(MockVatFixedFormat.of(loadMockDataJson()), MockVatFixedFormat.of(loadMockDataJson2()));
        byte[] bytes = fixedFormatConverter.convert(originModels);

        // when
        List<MockVatFixedFormat> convertedModels = fixedFormatConverter.convert(bytes);

        // then
        assertThat(originModels.size()).isEqualTo(convertedModels.size());
        for (int i = 0; i < originModels.size(); i++) {
            MockVatFixedFormat origin = (MockVatFixedFormat) originModels.get(i);
            MockVatFixedFormat converted = convertedModels.get(i);
            assertThat(origin).usingRecursiveComparison().isEqualTo(converted);
        }
    }

    @Test
    void testIsValidOfFixedFormatRecord() {
        // given
        String trueExpectedLine = "17I103200        1000000       100000";
        String falseExpectedLine = "17I1X3200        1000000       ";

        // when
        boolean expectedTrue = MockDetailRecord.isValid(trueExpectedLine, trueExpectedLine.getBytes());
        boolean expectedFalse = MockDetailRecord.isValid(falseExpectedLine, falseExpectedLine.getBytes());

        // then
        assertThat(expectedTrue).isTrue();
        assertThat(expectedFalse).isFalse();
    }

    @Test
    @SneakyThrows
    @DisplayName("중첩 구조의 model을 byte array로 변환할 수 있다")
    void convertNestedModelToByteArray() {
        // given
        FixedFormat fixedFormat = MockNestedFixedFormat.of(loadMockDataJson());

        // when
        byte[] bytes = nestedFixedFormatConverter.convert(fixedFormat);

        // then
        String[] lines = new String(bytes).split(CommonConstants.CRLF);
        assertThat(lines.length).isEqualTo(8);
    }

    @Test
    @SneakyThrows
    @DisplayName("중첩 구조의 byte array를 model로 변환할 수 있다")
    void convertNestedByteArrayToModel() {
        // given
        List<FixedFormat> fixedFormats = List.of(MockNestedFixedFormat.of(loadMockDataJson()), MockNestedFixedFormat.of(loadMockDataJson2()));
        byte[] bytes = nestedFixedFormatConverter.convert(fixedFormats);

        // when
        List<MockNestedFixedFormat> models = nestedFixedFormatConverter.convert(bytes);

        // then
        assertThat(models.size()).isEqualTo(2);
    }

    @Test
    @SneakyThrows
    @DisplayName("이중 중첩 구조에서도 model을 byte array로 변환할 수 있다")
    void convertDoubleNestedModelToByteArray() {
        // given
        List<FixedFormat> fixedFormats = List.of(MockDoubleNestedFixedFormat.of(loadMockDataJson()), MockDoubleNestedFixedFormat.of(loadMockDataJson2()));

        // when
        byte[] bytes = doubleNestedFixedFormatConverter.convert(fixedFormats);

        // then
        String[] lines = new String(bytes).split(CommonConstants.CRLF);
        assertThat(lines.length).isEqualTo(36);
    }

    @Test
    @SneakyThrows
    @DisplayName("이중 중첩 구조의 byte array를 model로 변환할 수 있다")
    void convertDoubleNestedByteArrayToModel() {
        // given
        List<FixedFormat> fixedFormats = List.of(MockDoubleNestedFixedFormat.of(loadMockDataJson()), MockDoubleNestedFixedFormat.of(loadMockDataJson2()));
        byte[] bytes = doubleNestedFixedFormatConverter.convert(fixedFormats);

        // when
        List<MockDoubleNestedFixedFormat> models = doubleNestedFixedFormatConverter.convert(bytes);

        // then
        assertThat(models.size()).isEqualTo(2);
    }

}
