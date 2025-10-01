package com.cmeza.spring.excel.repository.support.configurations.excel;

import com.cmeza.spring.excel.repository.support.configurations.AbstractConfiguration;
import com.cmeza.spring.excel.repository.support.enums.TableStyleValue;
import com.cmeza.spring.excel.repository.support.parsers.Parser;
import com.cmeza.spring.excel.repository.support.parsers.excel.ITableParser;
import lombok.Data;

@Data
public class TableConfiguration extends AbstractConfiguration<TableConfiguration> {
    private boolean table;
    private boolean filter;
    private TableStyleValue style;

    @Override
    public TableConfiguration cloneInstance() {
        TableConfiguration clone = new TableConfiguration();
        Parser.getInstance().getParser(ITableParser.class).merge(this, clone);
        return clone;
    }
}
