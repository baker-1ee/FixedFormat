package com.example.fixedformat.parser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RecordColumn {

    private String no;

    private String name;

    private String value;

    public static RecordColumn of(int index, String name, String value) {
        return RecordColumn.builder()
                .no(String.valueOf(index))
                .name(name)
                .value(value)
                .build();
    }
}
