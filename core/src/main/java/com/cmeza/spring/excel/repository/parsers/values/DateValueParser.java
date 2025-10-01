package com.cmeza.spring.excel.repository.parsers.values;

import com.cmeza.spring.excel.repository.support.parsers.values.ValueParser;
import org.apache.poi.xssf.streaming.SXSSFCell;

import java.util.Date;

public class DateValueParser implements ValueParser<Date> {

    @Override
    public boolean hasClassMatch(Class<?> clazz) {
        return clazz.equals(getValueClass());
    }

    @Override
    public boolean hasValueMatch(Object value) {
        return value instanceof Date;
    }

    @Override
    public Date getValue(Object value) {
        return (Date) value;
    }

    @Override
    public String getFormat() {
        return "yyyy-MM-dd HH:mm";
    }

    @Override
    public Class<Date> getValueClass() {
        return Date.class;
    }

    @Override
    public void apply(Object value, SXSSFCell cell) {
        cell.setCellValue(getValue(value));
    }
}
