package com.cmeza.spring.excel.repository.parsers.excel;

import com.cmeza.spring.excel.repository.support.annotations.support.Table;
import com.cmeza.spring.excel.repository.support.configurations.excel.TableConfiguration;
import com.cmeza.spring.excel.repository.support.enums.TableStyleValue;
import com.cmeza.spring.excel.repository.support.parsers.excel.ITableParser;

import java.util.Objects;

public class TableParser implements ITableParser<TableConfiguration, Table> {

    @Override
    public void parse(Table annotation, TableConfiguration tableConfiguration) {
        TableConfiguration tableConfigurationBean = new TableConfiguration();

        if (annotation.table()) {
            tableConfigurationBean.setTable(true);
        }
        if (annotation.filter()) {
            tableConfigurationBean.setFilter(true);
        }
        if (Objects.nonNull(annotation.style()) && !annotation.style().equals(TableStyleValue.NONE)) {
            tableConfigurationBean.setStyle(annotation.style());
        }

        merge(tableConfigurationBean, tableConfiguration);
    }

    @Override
    public void merge(TableConfiguration origin, TableConfiguration target) {
        if (origin.isTable()) {
            target.setTable(true);
        }
        if (origin.isFilter()) {
            target.setFilter(true);
        }
        if (Objects.nonNull(origin.getStyle()) && !origin.getStyle().equals(TableStyleValue.NONE)) {
            target.setStyle(origin.getStyle());
        }
    }
}
