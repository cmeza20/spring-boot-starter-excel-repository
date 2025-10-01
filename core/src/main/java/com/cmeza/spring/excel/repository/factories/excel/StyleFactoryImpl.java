package com.cmeza.spring.excel.repository.factories.excel;

import com.cmeza.spring.excel.repository.support.factories.excel.generics.IFactory;
import com.cmeza.spring.excel.repository.support.factories.excel.generics.ParentFactory;
import com.cmeza.spring.excel.repository.support.factories.excel.StyleFactory;
import com.cmeza.spring.excel.repository.factories.excel.generics.ParentFactoryImpl;
import com.cmeza.spring.excel.repository.support.interceptors.ToExcelInterceptor;
import com.cmeza.spring.excel.repository.support.mappers.ToExcelMapper;
import com.cmeza.spring.excel.repository.utils.ExcelUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.springframework.util.Assert;

import java.util.Objects;

public class StyleFactoryImpl extends ParentFactoryImpl<ParentFactory<?>> implements StyleFactory, IFactory<CellStyle, SXSSFWorkbook> {

    private String alias;
    private HorizontalAlignment horizontalAlignment;
    private VerticalAlignment verticalAlignment;
    private BorderStyle border;
    private FillPatternType fillPattern;
    private XSSFFont font;
    private Short borderColor;
    private Short dataFormat;
    private Boolean hidden;
    private Boolean locked;
    private Boolean quotePrefixed;
    private Boolean wrapText;
    private Boolean shrinkToFit;
    private Short rotation;
    private Short indention;
    private Short fillForegroundColor;
    private Short fillBackgroundColor;

    public StyleFactoryImpl(ParentFactory<?> parent) {
        super(parent);
    }

    @Override
    public StyleFactory withAlias(String alias) {
        this.alias = alias;
        return this;
    }

    @Override
    public StyleFactory withHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
        return this;
    }

    @Override
    public StyleFactory withVerticalAlignment(VerticalAlignment verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
        return this;
    }

    @Override
    public StyleFactory withBorderStyle(BorderStyle borderStyle) {
        this.border = borderStyle;
        return this;
    }

    @Override
    public StyleFactory withFillPatternType(FillPatternType fillPatternType) {
        this.fillPattern = fillPatternType;
        return this;
    }

    @Override
    public StyleFactory withFont(XSSFFont font) {
        this.font = font;
        return this;
    }

    @Override
    public StyleFactory withBorderColor(Short borderColor) {
        this.borderColor = borderColor;
        return this;
    }

    @Override
    public StyleFactory withDataFormat(Short dataFormat) {
        this.dataFormat = dataFormat;
        return this;
    }

    @Override
    public StyleFactory withHidden(Boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    @Override
    public StyleFactory withLocked(Boolean locked) {
        this.locked = locked;
        return this;
    }

    @Override
    public StyleFactory withQuotePrefixed(Boolean quotePrefixed) {
        this.quotePrefixed = quotePrefixed;
        return this;
    }

    @Override
    public StyleFactory withWrapText(Boolean wrapText) {
        this.wrapText = wrapText;
        return this;
    }

    @Override
    public StyleFactory withShrinkToFit(Boolean shrinkToFit) {
        this.shrinkToFit = shrinkToFit;
        return this;
    }

    @Override
    public StyleFactory withRotation(Short rotation) {
        this.rotation = rotation;
        return this;
    }

    @Override
    public StyleFactory withIndention(Short indention) {
        this.indention = indention;
        return this;
    }

    @Override
    public StyleFactory withFillForegroundColor(Short fillForegroundColor) {
        this.fillForegroundColor = fillForegroundColor;
        return this;
    }

    @Override
    public StyleFactory withFillBackgroundColor(Short fillBackgroundColor) {
        this.fillBackgroundColor = fillBackgroundColor;
        return this;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public CellStyle build(SXSSFWorkbook workbook, ToExcelInterceptor interceptor, ToExcelMapper toExcelMapper) {
        Assert.notNull(workbook, "Workbook must not be null");

        if (StringUtils.isEmpty(alias)) {
            alias = ExcelUtils.generateAlias("STYLE-");
        }

        CellStyle cellStyle = workbook.createCellStyle();

        if (Objects.nonNull(horizontalAlignment)) {
            cellStyle.setAlignment(horizontalAlignment);
        }
        if (Objects.nonNull(verticalAlignment)) {
            cellStyle.setVerticalAlignment(verticalAlignment);
        }
        if (Objects.nonNull(border)) {
            cellStyle.setBorderBottom(border);
            cellStyle.setBorderLeft(border);
            cellStyle.setBorderRight(border);
            cellStyle.setBorderTop(border);
        }
        if (Objects.nonNull(font)) {
            font.registerTo(workbook.getXSSFWorkbook().getStylesSource());
            cellStyle.setFont(font);
        }
        if (Objects.nonNull(dataFormat)) {
            cellStyle.setDataFormat(dataFormat);
        }
        if (Objects.nonNull(hidden)) {
            cellStyle.setHidden(hidden);
        }
        if (Objects.nonNull(locked)) {
            cellStyle.setLocked(locked);
        }
        if (Objects.nonNull(quotePrefixed)) {
            cellStyle.setQuotePrefixed(quotePrefixed);
        }
        if (Objects.nonNull(wrapText)) {
            cellStyle.setWrapText(wrapText);
        }
        if (Objects.nonNull(shrinkToFit)) {
            cellStyle.setShrinkToFit(shrinkToFit);
        }
        if (Objects.nonNull(rotation)) {
            cellStyle.setRotation(rotation);
        }
        if (Objects.nonNull(indention)) {
            cellStyle.setIndention(indention);
        }

        this.buildColor(cellStyle);

        return cellStyle;
    }

    @Override
    public int hashCode() {
        return alias.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (Objects.isNull(obj)) {
            return false;
        }
        StyleFactoryImpl factory = (StyleFactoryImpl)obj;
        return factory.getAlias().equals(alias);
    }

    private void buildColor(CellStyle cellStyle) {
        if (Objects.nonNull(borderColor)) {
            cellStyle.setBottomBorderColor(borderColor);
            cellStyle.setTopBorderColor(borderColor);
            cellStyle.setLeftBorderColor(borderColor);
            cellStyle.setRightBorderColor(borderColor);
            cellStyle.setRightBorderColor(borderColor);
        }
        if (Objects.nonNull(fillForegroundColor)) {
            cellStyle.setFillForegroundColor(fillForegroundColor);
        }
        if (Objects.nonNull(fillBackgroundColor)) {
            cellStyle.setFillBackgroundColor(fillBackgroundColor);
        }
        if (Objects.nonNull(fillPattern)) {
            cellStyle.setFillPattern(fillPattern);
        }
    }
}
