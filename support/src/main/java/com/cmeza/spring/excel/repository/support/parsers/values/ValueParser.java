package com.cmeza.spring.excel.repository.support.parsers.values;

import org.apache.poi.xssf.streaming.SXSSFCell;

public interface ValueParser<T> {
    boolean hasClassMatch(Class<?> clazz);

    boolean hasValueMatch(Object value);

    T getValue(Object value);

    String getFormat();

    Class<T> getValueClass();

    void apply(Object value, SXSSFCell cell);
}
