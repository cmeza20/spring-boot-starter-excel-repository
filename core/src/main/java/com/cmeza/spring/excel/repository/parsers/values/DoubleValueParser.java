package com.cmeza.spring.excel.repository.parsers.values;

import com.cmeza.spring.excel.repository.support.parsers.values.ValueParser;
import org.apache.poi.xssf.streaming.SXSSFCell;

public class DoubleValueParser implements ValueParser<Double> {

    @Override
    public boolean hasClassMatch(Class<?> clazz) {
        return clazz.equals(getValueClass());
    }

    @Override
    public boolean hasValueMatch(Object value) {
        return value instanceof Double;
    }

    @Override
    public Double getValue(Object value) {
        return Double.parseDouble(String.valueOf(value));
    }

    @Override
    public String getFormat() {
        return "#0.00";
    }

    @Override
    public Class<Double> getValueClass() {
        return Double.class;
    }

    @Override
    public void apply(Object value, SXSSFCell cell) {
        cell.setCellValue(getValue(value));
    }
}
