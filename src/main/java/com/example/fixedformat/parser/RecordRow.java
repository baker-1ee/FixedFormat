package com.example.fixedformat.parser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RecordRow {

    private String code;

    private List<RecordColumn> recordColumns;

    public static RecordRow of(String code, List<RecordColumn> recordColumns) {
        return RecordRow.builder()
                .code(code)
                .recordColumns(recordColumns)
                .build();
    }
}
