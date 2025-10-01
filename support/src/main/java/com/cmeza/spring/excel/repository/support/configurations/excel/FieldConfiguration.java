package com.cmeza.spring.excel.repository.support.configurations.excel;

import com.cmeza.spring.excel.repository.support.configurations.AbstractConfiguration;
import com.cmeza.spring.excel.repository.support.parsers.Parser;
import com.cmeza.spring.excel.repository.support.parsers.excel.IFieldParser;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.lang.reflect.Field;

@Data
@NoArgsConstructor
public class FieldConfiguration extends AbstractConfiguration<FieldConfiguration> {
    private String headerName;
    private String fieldName;
    private String mapping;
    private String dataFormat;
    private Integer position;
    private String styleAlias;
    private int[] styleRowPosition;
    private boolean ignored;
    private Field field;
    private int row;
    private int col;
    private boolean header;
    private StyleConfiguration style = new StyleConfiguration();

    @Override
    public FieldConfiguration cloneInstance() {
        FieldConfiguration clone = new FieldConfiguration();
        Parser.getInstance().getParser(IFieldParser.class).merge(this, clone);
        return clone;
    }

    public FieldConfiguration setStyle(StyleConfiguration style) {
        Assert.notNull(style, "StyleConfiguration cannot be null");
        this.style = style;
        return this;
    }
}
