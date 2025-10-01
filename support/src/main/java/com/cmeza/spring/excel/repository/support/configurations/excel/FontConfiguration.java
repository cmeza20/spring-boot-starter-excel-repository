package com.cmeza.spring.excel.repository.support.configurations.excel;

import com.cmeza.spring.excel.repository.support.configurations.AbstractConfiguration;
import com.cmeza.spring.excel.repository.support.parsers.Parser;
import com.cmeza.spring.excel.repository.support.parsers.excel.IFontParser;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.common.usermodel.fonts.FontCharset;
import org.apache.poi.ss.usermodel.FontFamily;
import org.apache.poi.ss.usermodel.FontUnderline;

import java.util.Objects;

@Data
@NoArgsConstructor
public class FontConfiguration extends AbstractConfiguration<FontConfiguration> {
    private String fontName;
    private Short fontHeight;
    private Short fontHeightInPoints;
    private Boolean italic;
    private Boolean strikeout;
    private Boolean bold;
    private Short color;
    private Short typeOffset;
    private FontUnderline underline;
    private FontCharset charset;
    private FontFamily fontFamily;

    public boolean isSet() {
        return StringUtils.isNotEmpty(fontName) ||
                Objects.nonNull(fontHeight) ||
                Objects.nonNull(fontHeightInPoints) ||
                Objects.nonNull(italic) ||
                Objects.nonNull(strikeout) ||
                Objects.nonNull(bold) ||
                Objects.nonNull(color) ||
                Objects.nonNull(typeOffset) ||
                Objects.nonNull(underline) ||
                Objects.nonNull(charset) ||
                Objects.nonNull(fontFamily);
    }

    @Override
    public FontConfiguration cloneInstance() {
        FontConfiguration clone = new FontConfiguration();
        Parser.getInstance().getParser(IFontParser.class).merge(this, clone);
        return clone;
    }
}
