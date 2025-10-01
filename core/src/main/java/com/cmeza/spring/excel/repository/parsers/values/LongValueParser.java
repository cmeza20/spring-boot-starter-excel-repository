package com.cmeza.spring.excel.repository.parsers.values;

import com.cmeza.spring.excel.repository.support.parsers.values.ValueParser;
import org.apache.poi.xssf.streaming.SXSSFCell;

public class LongValueParser implements ValueParser<Long> {

    @Override
    public boolean hasClassMatch(Class<?> clazz) {
        return clazz.equals(getValueClass());
    }

    @Override
    public boolean hasValueMatch(Object value) {
        return value instanceof Long;
    }

    @Override
    public Long getValue(Object value) {
        return Long.parseLong(String.valueOf(value));
    }

    @Override
    public String getFormat() {
        return "0";
    }

    @Override
    public Class<Long> getValueClass() {
        return Long.class;
    }

    @Override
    public void apply(Object value, SXSSFCell cell) {
        cell.setCellValue(getValue(value));
    }
}
