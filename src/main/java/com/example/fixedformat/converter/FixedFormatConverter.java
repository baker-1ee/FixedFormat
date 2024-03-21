package com.example.fixedformat.converter;

import com.example.fixedformat.aop.FixedFormatColumn;
import com.example.fixedformat.common.FixedFormat;
import com.example.fixedformat.common.FixedFormatRecord;
import com.example.fixedformat.exception.FixedFormatConverterException;
import com.example.fixedformat.formatter.FixedFormatFormatter;
import com.example.fixedformat.parser.FixedFormatReadParser;
import com.example.fixedformat.parser.RecordColumn;
import com.example.fixedformat.parser.RecordRow;
import com.example.fixedformat.parser.RecordType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
public class FixedFormatConverter<T extends FixedFormat> {

    @Getter
    private final String lineSeparator;
    @Getter
    private final String characterSet;
    private final FixedFormatFormatter formatter;
    private final FixedFormatReadParser parser;

    /**
     * FixedFormat list 를 byte array 로 변환
     */
    public byte[] convert(List<FixedFormat> fixedFormats) throws FixedFormatConverterException {
        try {
            StringBuilder sb = new StringBuilder();
            for (FixedFormat fixedFormat : fixedFormats) {
                sb.append(recursiveConverterToStringBuilder(fixedFormat));
            }
            return sb.toString().getBytes(characterSet);
        } catch (UnsupportedEncodingException e) {
            throw new FixedFormatConverterException("FixedFormatConverter characterSet is invalid");
        }
    }

    /**
     * FixedFormat 단건을 byte array로 convert
     */
    public byte[] convert(FixedFormat fixedFormat) throws FixedFormatConverterException {
        try {
            return recursiveConverterToStringBuilder(fixedFormat).toString().getBytes(characterSet);
        } catch (UnsupportedEncodingException e) {
            throw new FixedFormatConverterException("FixedFormatConverter characterSet is invalid");
        }
    }

    /**
     * byte array를 FixedFormat instance로 convert
     */
    public List<T> convert(byte[] bytes) throws FixedFormatConverterException {
        try {
            String contents = new String(bytes, characterSet);
            String[] lines = contents.split(lineSeparator);
            List<Record> records = convertFromLinesToFixedFormatRecords(lines);
            return makeFixedFormatList(records);
        } catch (IllegalAccessException e) {
            throw new FixedFormatConverterException("FixedFormatConverter reflection exception", e);
        } catch (UnsupportedEncodingException e) {
            throw new FixedFormatConverterException("FixedFormatConverter characterSet is invalid");
        } catch (Exception e) {
            throw new FixedFormatConverterException("FixedFormatConverter converting exception", e);
        }
    }

    /**
     * FixedFormat 단건을 RecordGroup으로 convert
     */
    public List<RecordRow> convertToRecordRows(FixedFormat fixedFormat) throws FixedFormatConverterException {
        List<RecordRow> recordRows = new ArrayList<>();
        try {
            for (Field recordField : fixedFormat.getClass().getDeclaredFields()) {
                recordField.setAccessible(true);

                if (FixedFormatRecord.class.isAssignableFrom(recordField.getType())) {
                    FixedFormatRecord fixedFormatRecord = (FixedFormatRecord) recordField.get(fixedFormat);
                    if (fixedFormatRecord != null) {
                        recordRows.add(getRecordRow(fixedFormatRecord));
                    }
                } else if (List.class.isAssignableFrom(recordField.getType())) {
                    List<FixedFormatRecord> fixedFormatRecords = (List<FixedFormatRecord>) recordField.get(fixedFormat);
                    if (fixedFormatRecords != null) {
                        for (FixedFormatRecord fixedFormatRecord : fixedFormatRecords) {
                            recordRows.add(getRecordRow(fixedFormatRecord));
                        }
                    }
                } else {
                    throw new FixedFormatConverterException("FixedFormatConverter FixedFormat field type is invalid");
                }
            }
        } catch (IllegalAccessException | UnsupportedEncodingException e) {
            throw new FixedFormatConverterException("FixedFormatConverter reflection exception", e);
        }
        return recordRows;
    }

    private RecordRow getRecordRow(FixedFormatRecord fixedFormatRecord) throws UnsupportedEncodingException, FixedFormatConverterException, IllegalAccessException {
        List<RecordColumn> recordColumns = new ArrayList<>();
        int index = 0;
        for (Field recordField : fixedFormatRecord.getClass().getDeclaredFields()) {
            recordField.setAccessible(true);
            FixedFormatColumn annotation = recordField.getAnnotation(FixedFormatColumn.class);
            if (annotation == null) continue;
            String formattedFieldValue = getFormattedFieldValue(fixedFormatRecord, recordField, annotation);
            recordColumns.add(RecordColumn.of(++index, annotation.name(), formattedFieldValue));
        }
        return RecordRow.of(fixedFormatRecord.getCode(), recordColumns);
    }

    // FixedFormat instance List를 생성 후 Generic type으로 변환
    private List<T> makeFixedFormatList(List<Record> records) throws FixedFormatConverterException, IllegalAccessException {
        return (List<T>) makeFixedFormatListRecursive(records, 0);
    }

    // FixedFormat instance List를 생성
    // Record List를 순회 하면서 header record가 나타날 때마다 FixedFormat instance를 생성하여 FixedFormat List에 추가 후 List 변환
    private List<FixedFormat> makeFixedFormatListRecursive(List<Record> records, int index) throws FixedFormatConverterException, IllegalAccessException {
        List<FixedFormat> fixedFormats = new ArrayList<>();
        while (index < records.size()) {
            if (records.get(index).isHeaderRecord()) {
                FixedFormat fixedFormat = makeFixedFormatRecursive(records, index);
                fixedFormats.add(fixedFormat);
            } else {
                break;
            }
        }
        return fixedFormats;
    }

    // FixedFormat instance를 생성
    // record list의 index 위치부터 순회하면서 FixedFormat instance를 생성하여 반환
    // FixedFormat의 member field 또는 FixedFormatRecord, List<FixedFormatRecord>, FixedFormat, List<FixedFormat> type만 올 수 있다.
    // FixedFormat의 member field로 FixedFormat 또는 List<FixedFormat> type이 존재하는 경우 재귀함수 호출을 통해 field값을 채워 준다.
    private FixedFormat makeFixedFormatRecursive(List<Record> records, int index) throws IllegalAccessException, FixedFormatConverterException {
        FixedFormat fixedFormat = records.get(index).getFixedFormatGenerator().get();
        // FixedFormat instance field 순회
        for (Field formatField : fixedFormat.getClass().getDeclaredFields()) {
            if (records.size() == 0) break;
            formatField.setAccessible(true);
            // 1. field type이 FixedFormatRecord인 경우
            if (FixedFormatRecord.class.isAssignableFrom(formatField.getType())) {
                FixedFormatRecord fixedFormatRecord = records.get(index).getFixedFormatRecord();
                if (formatField.getType().equals(fixedFormatRecord.getClass())) {
                    formatField.set(fixedFormat, fixedFormatRecord);
                    records.remove(index);
                }
            }
            // 2. field type이 List인 경우
            else if (List.class.isAssignableFrom(formatField.getType())) {
                FixedFormatRecord fixedFormatRecord = records.get(index).getFixedFormatRecord();
                // 2-1. List의 Type parameter가 FixedFormatRecord인 경우
                if (formatField.getGenericType().getTypeName().contains(fixedFormatRecord.getClass().getName())) {
                    List<FixedFormatRecord> fixedFormatRecords = getContinuousFixedFormatRecordList(records, index, fixedFormatRecord);
                    formatField.set(fixedFormat, fixedFormatRecords);
                }
                // 2-2. List의 Type parameter가 FixedFormat인 경우
                else {
                    // record List의 index 위치 부터 재귀 호출
                    List<FixedFormat> innerFixedFormatList = makeFixedFormatListRecursive(records, index);
                    formatField.set(fixedFormat, innerFixedFormatList);
                }
            }
            // 3. field type이 FixedFormat인 경우
            else if (FixedFormat.class.isAssignableFrom(formatField.getType())) {
                // record List의 index 위치부터 재귀 호출
                FixedFormat innerFixedFormat = makeFixedFormatRecursive(records, index);
                formatField.set(fixedFormat, innerFixedFormat);
            } else {
                throw new FixedFormatConverterException("FixedFormatConverter exception");
            }
        }
        return fixedFormat;
    }

    // record list를 index 위치 부터 순회하면서 연속으로 동일한 FixedFormatRecord 구현 instance가 연속 되는 List로 반환
    private List<FixedFormatRecord> getContinuousFixedFormatRecordList(List<Record> recordList, int index, FixedFormatRecord startFixedFormatRecord) {
        List<FixedFormatRecord> fixedFormatRecords = new ArrayList<>();
        fixedFormatRecords.add(startFixedFormatRecord);
        recordList.remove(index);

        while (index < recordList.size()) {
            if (recordList.get(index).getFixedFormatRecord().getClass().equals(startFixedFormatRecord.getClass())) {
                fixedFormatRecords.add(recordList.get(index).getFixedFormatRecord());
                recordList.remove(index);
            } else {
                break;
            }
        }
        return fixedFormatRecords;
    }

    private List<Record> convertFromLinesToFixedFormatRecords(String[] lines) throws UnsupportedEncodingException, FixedFormatConverterException, IllegalAccessException {
        List<Record> records = new ArrayList<>();
        for (String line : lines) {
            byte[] lineBytes = line.getBytes(characterSet);
            RecordType recordType = parser.findMatchedRecord(line, lineBytes);
            FixedFormatRecord fixedFormatRecord = getConvertedFixedFormatRecord(lineBytes, recordType);

            Record record = new Record(recordType.isHeaderRecord(), fixedFormatRecord, recordType.getFixedFormatGenerator());
            records.add(record);
        }
        return records;
    }

    private FixedFormatRecord getConvertedFixedFormatRecord(byte[] lineBytes, RecordType recordType) throws UnsupportedEncodingException, FixedFormatConverterException, IllegalAccessException {
        FixedFormatRecord fixedFormatRecord = recordType.getFixedFormatRecordGenerator().get();
        int index = 0;
        for (Field recordField : fixedFormatRecord.getClass().getDeclaredFields()) {
            recordField.setAccessible(true);
            FixedFormatColumn annotation = recordField.getAnnotation(FixedFormatColumn.class);
            if (annotation == null) continue;
            byte[] fieldBytes = new byte[annotation.size()];
            for (int i = 0; i < fieldBytes.length; i++) {
                fieldBytes[i] = lineBytes[index++];
            }
            String stringValue = new String(fieldBytes, characterSet);
            String fieldValue = formatter.strip(stringValue, recordField);

            if (recordField.getType().equals(Long.class)) {
                recordField.set(fixedFormatRecord, fieldValue.equals("") ? 0 : Long.parseLong(fieldValue));
            } else {
                recordField.set(fixedFormatRecord, fieldValue);
            }
        }

        return fixedFormatRecord;
    }

    // FixedFormat instance 를 StringBuilder 로 변환
    // FixedFormat member field 가 FixedFormat or List<FixedFormat> 인 경우에는 재귀 호출
    private StringBuilder recursiveConverterToStringBuilder(FixedFormat fixedFormat) throws FixedFormatConverterException, UnsupportedEncodingException {
        StringBuilder resultSb = new StringBuilder();
        try {
            // Fixedformat field 순회
            for (Field recordField : fixedFormat.getClass().getDeclaredFields()) {
                recordField.setAccessible(true);
                // 1. field type 이 FixedFormatRecord 인 경우
                if (FixedFormatRecord.class.isAssignableFrom(recordField.getType())) {
                    FixedFormatRecord fixedFormatRecord = (FixedFormatRecord) recordField.get(fixedFormat);
                    if (fixedFormatRecord != null) {
                        resultSb.append(convertToStringBuilder(fixedFormatRecord));
                    }
                }
                // 2. filed type이 List 인 경우
                else if (List.class.isAssignableFrom(recordField.getType())) {
                    List<Object> recordFields = (List<Object>) recordField.get(fixedFormat);
                    // 2-1. List의 Type parameter가 FixedFormatRecord인 경우
                    List<FixedFormatRecord> fixedFormatRecords = recordFields.stream()
                            .filter(e -> e instanceof FixedFormatRecord)
                            .map(e -> (FixedFormatRecord) e)
                            .toList();
                    for (FixedFormatRecord fixedFormatRecord : fixedFormatRecords) {
                        resultSb.append(convertToStringBuilder(fixedFormatRecord));
                    }
                    // 2-2. List의 Type parameter가 FixedFormat인 경우
                    List<FixedFormat> fixedFormats = recordFields.stream()
                            .filter(e -> e instanceof FixedFormat)
                            .map(e -> (FixedFormat) e)
                            .toList();
                    for (FixedFormat innerFixedFormat : fixedFormats) {
                        // 재귀 호출
                        resultSb.append(recursiveConverterToStringBuilder(innerFixedFormat));
                    }
                }
                // 3. filed type이 FixedFormat인 경우
                else if (FixedFormat.class.isAssignableFrom(recordField.getType())) {
                    FixedFormat innerFixedFormat = (FixedFormat) recordField.get(fixedFormat);
                    // 재귀 호출
                    resultSb.append(recursiveConverterToStringBuilder(innerFixedFormat));
                } else {
                    throw new FixedFormatConverterException("FixedFormatConverter FixedFormat field type is invalid");
                }
            }
        } catch (IllegalAccessException | FixedFormatConverterException | UnsupportedEncodingException e) {
            throw new FixedFormatConverterException("FixedFormatConverter reflection exception", e);
        }
        return resultSb;
    }

    // FixedFormatRecord instance 를 StringBuilder 로 변환
    private StringBuilder convertToStringBuilder(FixedFormatRecord fixedFormatRecord) throws UnsupportedEncodingException, FixedFormatConverterException, IllegalAccessException {
        StringBuilder sb = new StringBuilder();
        // FixedFormatRecord field 순회
        for (Field recordField : fixedFormatRecord.getClass().getDeclaredFields()) {
            recordField.setAccessible(true);
            FixedFormatColumn annotation = recordField.getAnnotation(FixedFormatColumn.class);
            if (annotation == null) continue;
            String formattedFieldValue = getFormattedFieldValue(fixedFormatRecord, recordField, annotation);
            sb.append(formattedFieldValue);
        }
        sb.append(lineSeparator);

        return sb;
    }

    private String getFormattedFieldValue(FixedFormatRecord fixedFormatRecord,
                                          Field recordField,
                                          FixedFormatColumn annotation) throws IllegalAccessException, UnsupportedEncodingException, FixedFormatConverterException {
        String fieldValue = getRecordFieldValue(fixedFormatRecord, recordField);
        int paddingLength = getPaddingLength(fieldValue, annotation.size());
        return formatter.pad(fieldValue, recordField, paddingLength);
    }

    private String getRecordFieldValue(FixedFormatRecord fixedFormatRecord, Field recordField) throws IllegalAccessException {
        return String.valueOf(
                Objects.nonNull(recordField.get(fixedFormatRecord)) ?
                        recordField.get(fixedFormatRecord) : ""
        );
    }

    public int getPaddingLength(String fieldValue, int annotationSize) throws UnsupportedEncodingException {
        return annotationSize - fieldValue.getBytes(characterSet).length + fieldValue.length();
    }


}
