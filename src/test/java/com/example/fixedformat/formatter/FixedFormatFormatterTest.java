package com.example.fixedformat.formatter;

import com.example.fixedformat.common.CommonConstants;
import com.example.fixedformat.component.FixedFormatFactory;
import com.example.fixedformat.converter.FixedFormatConverter;
import com.example.fixedformat.converter.mock.fixedformat.MockVatFixedFormat;
import com.example.fixedformat.converter.mock.parser.MockFixedFormatReadParser;
import com.example.fixedformat.exception.FixedFormatConverterException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(classes = FixedFormatFactory.class)
class FixedFormatFormatterTest {

    @Autowired
    FixedFormatFactory fixedFormatFactory;

    @Test
    @DisplayName("Minus로 시작하면 stripStart 해도 0이 제거 되지 않는다.")
    void whenStripStartAboutMinusStringThenThereIsNoChanges() {
        // given
        String value = "-000000500";

        // when
        String actual = StringUtils.stripStart(value, "0");

        // then
        assertThat(actual).isEqualTo(value);
    }

    @Test
    @DisplayName("Minus로 시작하면 Long.parsLong 할 때, 0이 제거 된다")
    void whenStartsMinusAndParseAsLongThenZeroIsStriped() {
        // given
        String value = "-000000500";

        // when
        Long actual = Long.parseLong(value);

        // then
        assertThat(actual).isEqualTo(-500);
    }

    @Test
    @DisplayName("음수를 문자열로 패딩 할 때, 마이너스 부호를 가장 앞으로 빼보자")
    void whenPaddingNegativeNumberWithStringLetsSubtractNegativeSignToTheFront() {
        // given
        long number = -500;
        int length = 6;

        // when
        String actual = "-" + StringUtils.leftPad(String.valueOf(number * -1), length - 1, "0");

        // then
        assertThat(actual).isEqualTo("-00500");
    }

    @Test
    @DisplayName("음수를 문자열로 패딩 할 때, 마이너스 부호를 안쪽에 표시하자")
    void whenPaddingNegativeNumberWithStringMarkTheNegativeSignInside() {
        // given
        long number = -500;
        int length = 6;

        // when
        String actual = StringUtils.leftPad(String.valueOf(number), length, "0");

        // then
        assertThat(actual).isEqualTo("00-500");
    }

    @Test
    @DisplayName("Minus 부호가 내부에 있는 문자열을 strip 하자")
    void letsStripTheStringWithMinusSignInside() {
        // given
        String value = "00-500";

        // when
        String actual = StringUtils.stripStart(value, "0");

        // then
        assertThat(actual).isEqualTo("-500");
    }

    @Test
    @DisplayName("문자열 00을 strip 하면 빈 문자열이 된다")
    void ifString00IsStripedItBecomesAnEmptyString() {
        // given
        String value = "00";

        // when
        String fieldValue = StringUtils.stripStart(value, "0");
        Long actual = fieldValue.equals("") ? 0 : Long.parseLong(fieldValue);

        // then
        assertThat(actual).isEqualTo(0);
    }

    @Test
    @DisplayName("문자열을 rightPad 해보자")
    void letsPlayRightPadOnTheString() {
        // given
        String value = "김세무";

        // when
        String padValue = StringUtils.rightPad(value, 5, " ");

        // then
        assertThat(padValue).isEqualTo("김세무  ");
    }

    @Test
    @DisplayName("문자열을 stripEnd 해보자")
    void letsPlayStripEndOnTheString() {
        // given
        String value = "김세무  ";

        // when
        String stripValue = StringUtils.stripEnd(value, " ");

        // then
        assertThat(stripValue).isEqualTo("김세무");
    }

    @SneakyThrows
    @Test
    @DisplayName("characterSet 에 따라 패딩할 문자열 길이가 다르다")
    void theLengthOfTheStringToPadIsDifferentDependingOnTheCharacterSet() {
        // given
        int byteSize = 10;
        String value = "김3세무";

        byte[] bytesUtf8 = value.getBytes(StandardCharsets.UTF_8);
        byte[] bytesEucKr = value.getBytes(CommonConstants.EUC_KR);

        assertThat(bytesUtf8.length).isEqualTo(10);
        assertThat(bytesEucKr.length).isEqualTo(7);

        // when
        String padValueUtf8 = StringUtils.leftPad(value, byteSize - bytesUtf8.length + value.length(), "X");
        String padValueEucKr = StringUtils.leftPad(value, byteSize - bytesEucKr.length + value.length(), "X");

        // then
        assertThat(padValueUtf8).isEqualTo("김3세무");
        assertThat(padValueUtf8.getBytes(StandardCharsets.UTF_8).length).isEqualTo(byteSize);

        assertThat(padValueEucKr).isEqualTo("XXX김3세무");
        assertThat(padValueEucKr.getBytes(CommonConstants.EUC_KR).length).isEqualTo(byteSize);
    }

    @SneakyThrows
    @Test
    @DisplayName("characterSet에 따라 한글 바이트 길이가 다르므로 문자열 패딩할 때 패딩 해야할 문자열 길이를 계산 해야한다")
    void calculatePaddingLengthByCharacterSet() {
        // given
        FixedFormatConverter<MockVatFixedFormat> converterEucKr = fixedFormatFactory.getConverter(CommonConstants.EUC_KR, new MockFixedFormatReadParser());
        FixedFormatConverter<MockVatFixedFormat> converterUtf8 = fixedFormatFactory.getConverter(CommonConstants.UTF_8, new MockFixedFormatReadParser());

        String fieldValue = "abc 한글";
        int annotationSize = 30;

        assertThat(fieldValue.getBytes(CommonConstants.EUC_KR).length).isEqualTo(8);
        assertThat(fieldValue.getBytes(StandardCharsets.UTF_8).length).isEqualTo(10);

        // when
        int paddingLengthEucKr = converterEucKr.getPaddingLength(fieldValue, annotationSize);
        int paddingLengthUtf8 = converterUtf8.getPaddingLength(fieldValue, annotationSize);
        String paddedValueEucKr = StringUtils.leftPad(fieldValue, paddingLengthEucKr, "X");
        String paddedValueUtf8 = StringUtils.leftPad(fieldValue, paddingLengthUtf8, "X");

        // then
        assertThat(paddingLengthEucKr).isEqualTo(30 - 8 + 6);
        assertThat(paddingLengthUtf8).isEqualTo(30 - 10 + 6);
        assertThat(paddedValueEucKr.getBytes(CommonConstants.EUC_KR).length).isEqualTo(annotationSize);
        assertThat(paddedValueUtf8.getBytes(StandardCharsets.UTF_8).length).isEqualTo(annotationSize);
    }

    @Test
    @SneakyThrows
    @DisplayName("바이트 전체 길이 초과하면 자르기")
    void trimIfExceedsByteLength() {
        // given
        String text = "관리사무소";

        // then
        assertThat(substringByBytes(text, 0, 20, CommonConstants.EUC_KR)).isEqualTo("관리사무소");
        assertThat(substringByBytes(text, 0, 9, CommonConstants.EUC_KR)).isEqualTo("관리사무");
        assertThat(substringByBytes(text, 0, 8, CommonConstants.EUC_KR)).isEqualTo("관리사무");
    }

    private String substringByBytes(String str, int beginBytes, int endBytes, String characterSet) throws FixedFormatConverterException {
        if (str == null || str.length() == 0) {
            return "";
        }

        if (beginBytes < 0) {
            beginBytes = 0;
        }

        if (endBytes < 1) {
            return "";
        }

        int len = str.length();

        int beginIndex = -1;
        int endIndex = 0;

        int curBytes = 0;
        String ch;
        for (int i = 0; i < len; i++) {
            ch = str.substring(i, i + 1);
            try {
                curBytes += ch.getBytes(characterSet).length;
            } catch (UnsupportedEncodingException e) {
                throw new FixedFormatConverterException("FixedFormatConverter Exception - substringByBytes method");
            }

            if (beginIndex == -1 && curBytes >= beginBytes) {
                beginIndex = i;
            }

            if (curBytes > endBytes) {
                break;
            } else {
                endIndex = i + 1;
            }
        }

        return str.substring(beginIndex, endIndex);
    }

}
