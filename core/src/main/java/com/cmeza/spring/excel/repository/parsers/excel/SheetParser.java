package com.cmeza.spring.excel.repository.parsers.excel;

import com.cmeza.spring.excel.repository.support.annotations.model.Sheet;
import com.cmeza.spring.excel.repository.support.annotations.support.Mapping;
import com.cmeza.spring.excel.repository.support.configurations.excel.SheetConfiguration;
import com.cmeza.spring.excel.repository.support.parsers.Parser;
import com.cmeza.spring.excel.repository.support.parsers.excel.ISheetParser;
import org.apache.commons.lang3.StringUtils;

public class SheetParser implements ISheetParser<SheetConfiguration, Sheet> {
    private static final Parser parser = Parser.getInstance();

    @Override
    public void parse(Sheet annotation, SheetConfiguration sheetConfiguration, boolean isBean) {
        if (StringUtils.isNotEmpty(annotation.name()) && !isBean) {
            sheetConfiguration.setSheetName(annotation.name());
        }

        if (!annotation.autosize()) {
            sheetConfiguration.setAutoSize(false);
        }

        parser.getParser(HeaderParser.class).parse(annotation.header(), sheetConfiguration.getHeader());
        parser.getParser(TableParser.class).parse(annotation.table(), sheetConfiguration.getTable());

        for (Mapping mapping : annotation.mappings()) {
            parser.getParser(MappingParser.class).parse(mapping, sheetConfiguration);
        }
    }

    @Override
    public void merge(SheetConfiguration origin, SheetConfiguration target) {
        if (StringUtils.isNotEmpty(origin.getSheetName())) {
            target.setSheetName(origin.getSheetName());
        }
        if (!origin.isAutoSize()) {
            target.setAutoSize(false);
        }
        if (!origin.getStyles().isEmpty()) {
            target.getStyles().addAll(origin.getStyles());
        }

        parser.getParser(HeaderParser.class).merge(origin.getHeader(), target.getHeader());
        parser.getParser(TableParser.class).merge(origin.getTable(), target.getTable());
        parser.getParser(MappingParser.class).merge(origin, target);
    }
}
