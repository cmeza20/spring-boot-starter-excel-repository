package com.cmeza.spring.excel.repository.support.configurations.excel;

import com.cmeza.spring.excel.repository.support.configurations.AbstractConfiguration;
import com.cmeza.spring.excel.repository.support.parsers.Parser;
import com.cmeza.spring.excel.repository.support.parsers.excel.ISheetParser;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Data
public class SheetConfiguration extends AbstractConfiguration<SheetConfiguration> {
    private String sheetName;
    private boolean autoSize = true;
    private HeaderConfiguration header = new HeaderConfiguration();
    private TableConfiguration table = new TableConfiguration();

    @Setter(AccessLevel.PRIVATE)
    private List<FieldConfiguration> mappings = new LinkedList<>();

    @Setter(AccessLevel.PRIVATE)
    private List<StyleConfiguration> styles = new ArrayList<>();

    public SheetConfiguration addStyle(StyleConfiguration style) {
        Assert.notNull(style, "StyleConfiguration can not be null");
        Assert.hasLength(style.getAlias(), "StyleConfiguration::Alias is empty");
        styles.add(style);
        return this;
    }

    public SheetConfiguration setHeader(HeaderConfiguration header) {
        Assert.notNull(header, "HeaderConfiguration can not be null");
        this.header = header;
        return this;
    }

    public SheetConfiguration setTable(TableConfiguration table) {
        Assert.notNull(table, "TableConfiguration can not be null");
        this.table = table;
        return this;
    }

    public SheetConfiguration addMapping(String fieldName) {
        this.addMapping(fieldName, null, null);
        return this;
    }

    public SheetConfiguration addMapping(String fieldName, String headerName) {
        Assert.hasLength(headerName, "HeaderName is empty");
        this.addMapping(fieldName, headerName, null);
        return this;
    }

    public SheetConfiguration addMapping(String fieldName, String headerName, String styleAlias) {
        Assert.hasLength(fieldName, "FieldName is empty");
        mappings.add(new FieldConfiguration().setFieldName(fieldName).setHeaderName(headerName).setStyleAlias(styleAlias));
        return this;
    }

    @Override
    public SheetConfiguration cloneInstance() {
        SheetConfiguration clone = new SheetConfiguration();
        Parser.getInstance().getParser(ISheetParser.class).merge(this, clone);
        return clone;
    }
}
