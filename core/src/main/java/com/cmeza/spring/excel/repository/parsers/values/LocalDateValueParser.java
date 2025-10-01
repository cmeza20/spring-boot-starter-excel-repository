package com.cmeza.spring.excel.repository.parsers.values;

import com.cmeza.spring.excel.repository.support.parsers.values.ValueParser;
import org.apache.poi.xssf.streaming.SXSSFCell;

import java.time.LocalDate;

public class LocalDateValueParser implements ValueParser<LocalDate> {

    @Override
    public boolean hasClassMatch(Class<?> clazz) {
        return clazz.equals(getValueClass());
    }

    @Override
    public boolean hasValueMatch(Object value) {
        return value instanceof LocalDate;
    }

    @Override
    public LocalDate getValue(Object value) {
        return (LocalDate) value;
    }

    @Override
    public String getFormat() {
        return "yyyy-MM-dd";
    }

    @Override
    public Class<LocalDate> getValueClass() {
        return LocalDate.class;
    }

    @Override
    public void apply(Object value, SXSSFCell cell) {
        cell.setCellValue(getValue(value));
    }
}
