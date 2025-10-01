package com.cmeza.spring.excel.repository.parsers.values;

import com.cmeza.spring.excel.repository.support.parsers.values.ValueParser;
import org.apache.poi.xssf.streaming.SXSSFCell;

public class BooleanValueParser implements ValueParser<Boolean> {

    @Override
    public boolean hasClassMatch(Class<?> clazz) {
        return clazz.equals(getValueClass());
    }

    @Override
    public boolean hasValueMatch(Object value) {
        return value instanceof Boolean;
    }

    @Override
    public Boolean getValue(Object value) {
        return (Boolean) value;
    }

    @Override
    public String getFormat() {
        return null;
    }

    @Override
    public Class<Boolean> getValueClass() {
        return Boolean.class;
    }

    @Override
    public void apply(Object value, SXSSFCell cell) {
        cell.setCellValue(getValue(value));
    }
}
