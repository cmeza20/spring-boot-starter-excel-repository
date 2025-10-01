package com.cmeza.spring.excel.repository.parsers.excel;

import com.cmeza.spring.excel.repository.support.configurations.excel.FieldConfiguration;
import com.cmeza.spring.excel.repository.support.parsers.Parser;
import com.cmeza.spring.excel.repository.support.parsers.excel.IFieldParser;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class FieldParser implements IFieldParser<FieldConfiguration> {

    private static final Parser parser = Parser.getInstance();

    @Override
    public void merge(FieldConfiguration origin, FieldConfiguration target) {
        if (StringUtils.isNotEmpty(origin.getHeaderName())) {
            target.setHeaderName(origin.getHeaderName());
        }
        if (StringUtils.isNotEmpty(origin.getFieldName())) {
            target.setFieldName(origin.getFieldName());
        }
        if (StringUtils.isNotEmpty(origin.getMapping())) {
            target.setMapping(origin.getMapping());
        }
        if (StringUtils.isNotEmpty(origin.getDataFormat())) {
            target.setDataFormat(origin.getDataFormat());
        }
        if (Objects.nonNull(origin.getPosition())) {
            target.setPosition(origin.getPosition());
        }
        if (StringUtils.isNotEmpty(origin.getStyleAlias())) {
            target.setStyleAlias(origin.getStyleAlias());
        }

        origin.setStyleRowPosition(ArrayUtils.addAll(origin.getStyleRowPosition(), target.getStyleRowPosition()));

        if (origin.isIgnored()) {
            target.setIgnored(true);
        }

        if (Objects.nonNull(origin.getField())) {
            target.setField(origin.getField());
        }
        if (origin.getRow() != 0) {
            target.setRow(origin.getRow());
        }
        if (origin.getCol() != 0) {
            target.setRow(origin.getCol());
        }
        if (origin.isHeader()) {
            target.setHeader(true);
        }

        parser.getParser(StyleParser.class).merge(origin.getStyle(), target.getStyle());
    }
}
