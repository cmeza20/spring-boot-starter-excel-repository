package com.cmeza.spring.excel.repository.support.configurations.excel;

import com.cmeza.spring.excel.repository.support.configurations.AbstractConfiguration;
import com.cmeza.spring.excel.repository.support.parsers.Parser;
import com.cmeza.spring.excel.repository.support.parsers.excel.IStyleParser;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.springframework.util.Assert;

import java.util.Objects;
import java.util.function.Consumer;

@Data
@NoArgsConstructor
public class StyleConfiguration extends AbstractConfiguration<StyleConfiguration> {
    private String alias;
    private HorizontalAlignment horizontalAlignment;
    private VerticalAlignment verticalAlignment;
    private BorderStyle borderStyle;
    private FillPatternType fillPatternType;
    private Short borderColor;
    private Short dataFormat;
    private Boolean hidden;
    private Boolean locked;
    private Boolean quotePrefixed;
    private Boolean wrapText;
    private Boolean shrinkToFit;
    private Short rotation;
    private Short indention;
    private Short fillBackgroundColor;
    private Short fillForegroundColor;
    private FontConfiguration font = new FontConfiguration();

    public boolean hasAlias() {
        return StringUtils.isNotEmpty(alias);
    }

    public boolean isSet() {
        return Objects.nonNull(horizontalAlignment) ||
                Objects.nonNull(verticalAlignment) ||
                Objects.nonNull(borderStyle) ||
                Objects.nonNull(fillPatternType) ||
                Objects.nonNull(borderColor) ||
                Objects.nonNull(dataFormat) ||
                Objects.nonNull(hidden) ||
                Objects.nonNull(locked) ||
                Objects.nonNull(quotePrefixed) ||
                Objects.nonNull(wrapText) ||
                Objects.nonNull(shrinkToFit) ||
                Objects.nonNull(rotation) ||
                Objects.nonNull(indention) ||
                Objects.nonNull(fillBackgroundColor) ||
                Objects.nonNull(fillForegroundColor) ||
                font.isSet();
    }

    public StyleConfiguration setFont(FontConfiguration font) {
        Assert.notNull(font, "Font must not be null");
        this.font = font;
        return this;
    }

    public StyleConfiguration withFont(Consumer<FontConfiguration> consumer) {
        Assert.notNull(consumer, "Consumer must not be null");
        consumer.accept(font);
        return this;
    }

    @Override
    public StyleConfiguration cloneInstance() {
        StyleConfiguration clone = new StyleConfiguration();
        Parser.getInstance().getParser(IStyleParser.class).merge(this, clone);
        return clone;
    }
}
