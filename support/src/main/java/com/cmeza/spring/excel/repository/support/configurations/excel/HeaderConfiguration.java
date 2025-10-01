package com.cmeza.spring.excel.repository.support.configurations.excel;

import com.cmeza.spring.excel.repository.support.configurations.AbstractConfiguration;
import com.cmeza.spring.excel.repository.support.parsers.Parser;
import com.cmeza.spring.excel.repository.support.parsers.excel.IHeaderParser;
import lombok.Data;

@Data
public class HeaderConfiguration extends AbstractConfiguration<HeaderConfiguration> {
    private boolean header = true;
    private boolean freeze;
    private String styleAlias;
    private int heightMultiplier = 1;

    @Override
    public HeaderConfiguration cloneInstance() {
        HeaderConfiguration clone = new HeaderConfiguration();
        Parser.getInstance().getParser(IHeaderParser.class).merge(this, clone);
        return clone;
    }
}
