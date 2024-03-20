package com.example.fixedformat.common;

public interface FixedFormatRecord {
    default String getCode() {
        return "";
    }
}
