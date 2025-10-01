package com.cmeza.spring.excel.repository.parsers.values;

import com.cmeza.spring.excel.repository.support.parsers.values.ValueParser;
import org.apache.poi.xssf.streaming.SXSSFCell;

import java.time.LocalDateTime;

public class LocalDateTimeValueParser implements ValueParser<LocalDateTime> {

    @Override
    public boolean hasClassMatch(Class<?> clazz) {
        return clazz.equals(getValueClass());
    }

    @Override
    public boolean hasValueMatch(Object value) {
        return value instanceof LocalDateTime;
    }

    @Override
    public LocalDateTime getValue(Object value) {
        return (LocalDateTime) value;
    }

    @Override
    public String getFormat() {
        return "yyyy-MM-dd HH:mm";
    }

    @Override
    public Class<LocalDateTime> getValueClass() {
        return LocalDateTime.class;
    }

    @Override
    public void apply(Object value, SXSSFCell cell) {
        cell.setCellValue(getValue(value));
    }
}
