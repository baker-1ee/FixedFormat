package com.example.fixedformat.parser;

import com.example.fixedformat.converter.mock.parser.MockFixedFormatReadParser;
import com.example.fixedformat.exception.FixedFormatConverterException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FixedFormatReadParserTest {

    @Test
    @SneakyThrows
    void testFindMatchedRecord() {
        // given
        String line = "17I103200        1000000       100000";
        FixedFormatReadParser parser = new MockFixedFormatReadParser();

        // when
        RecordType matchedRecord = parser.findMatchedRecord(line, line.getBytes());

        // then
        assertThat(matchedRecord.getName()).isEqualTo("일반 과세자 신고서");
    }

    @Test
    @SneakyThrows
    void whenNotExistMatchedRecordTypeThenOccurException() {
        // given
        String line = "17I103200 누적 길이가 다르게";
        FixedFormatReadParser parser = new MockFixedFormatReadParser();

        // when & then
        assertThatThrownBy(
                () -> parser.findMatchedRecord(line, line.getBytes())
        ).isInstanceOf(FixedFormatConverterException.class).extracting("message").isEqualTo("FixedFormatReadParser exception");

    }


}
