package com.cmeza.spring.excel.repository.parsers.excel;

import com.cmeza.spring.excel.repository.support.annotations.model.Style;
import com.cmeza.spring.excel.repository.support.configurations.excel.FontConfiguration;
import com.cmeza.spring.excel.repository.support.configurations.excel.StyleConfiguration;
import com.cmeza.spring.excel.repository.support.exceptions.ProcessorException;
import com.cmeza.spring.excel.repository.support.parsers.Parser;
import com.cmeza.spring.excel.repository.support.parsers.excel.IStyleParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;

import java.util.Objects;

public class StyleParser implements IStyleParser<StyleConfiguration, Style> {

    private static final Parser parser = Parser.getInstance();

    @Override
    public void parse(Style style, StyleConfiguration styleConfiguration, boolean isBean) {
        if (StringUtils.isNotEmpty(style.name())) {
            if (!isBean) {
                styleConfiguration.setAlias(style.name());
            }
        } else {
            throw new ProcessorException("Excel class style annotation [name] required");
        }

        if (!style.borderStyle().equals(BorderStyle.NONE)) {
            styleConfiguration.setBorderStyle(style.borderStyle());
        }

        if (style.dataFormat() != -1) {
            styleConfiguration.setDataFormat(style.dataFormat());
        }
        if (style.hidden()) {
            styleConfiguration.setHidden(true);
        }
        if (style.locked()) {
            styleConfiguration.setLocked(true);
        }
        if (style.quotePrefixed()) {
            styleConfiguration.setQuotePrefixed(true);
        }
        if (style.wrapText()) {
            styleConfiguration.setWrapText(true);
        }
        if (style.shrinkToFit()) {
            styleConfiguration.setShrinkToFit(true);
        }

        this.parseLocation(style, styleConfiguration);
        this.parseColor(style, styleConfiguration);

        FontParser fontParser = parser.getParser(FontParser.class);

        this.parseFont(style, styleConfiguration, fontParser);

        if (!styleConfiguration.isSet()) {
            throw new ProcessorException(style.name() + " style is not set");
        }
    }

    @Override
    public void merge(StyleConfiguration origin, StyleConfiguration target) {
        if (StringUtils.isNotEmpty(origin.getAlias())) {
            target.setAlias(origin.getAlias());
        }
        if (Objects.nonNull(origin.getBorderStyle())) {
            target.setBorderStyle(origin.getBorderStyle());
        }
        if (Objects.nonNull(origin.getDataFormat())) {
            target.setDataFormat(origin.getDataFormat());
        }
        if (Objects.nonNull(origin.getHidden())) {
            target.setHidden(origin.getHidden());
        }
        if (Objects.nonNull(origin.getLocked())) {
            target.setLocked(origin.getLocked());
        }
        if (Objects.nonNull(origin.getQuotePrefixed())) {
            target.setQuotePrefixed(origin.getQuotePrefixed());
        }
        if (Objects.nonNull(origin.getWrapText())) {
            target.setWrapText(origin.getWrapText());
        }
        if (Objects.nonNull(origin.getShrinkToFit())) {
            target.setShrinkToFit(origin.getShrinkToFit());
        }
        if (Objects.nonNull(origin.getRotation())) {
            target.setRotation(origin.getRotation());
        }
        if (Objects.nonNull(origin.getIndention())) {
            target.setIndention(origin.getIndention());
        }

        this.mergeColor(origin, target);

        FontParser fontParser = parser.getParser(FontParser.class);
        fontParser.merge(origin.getFont(), target.getFont());
    }

    private void mergeColor(StyleConfiguration origin, StyleConfiguration target) {
        if (Objects.nonNull(origin.getBorderColor())) {
            target.setBorderColor(origin.getBorderColor());
        }
        if (Objects.nonNull(origin.getFillBackgroundColor())) {
            target.setFillBackgroundColor(origin.getFillBackgroundColor());
        }
        if (Objects.nonNull(origin.getFillForegroundColor())) {
            target.setFillForegroundColor(origin.getFillForegroundColor());
        }
        if (Objects.nonNull(origin.getFillPatternType())) {
            target.setFillPatternType(origin.getFillPatternType());
        }
    }

    private void parseColor(Style style, StyleConfiguration styleConfiguration) {
        if (!style.borderColor().equals(IndexedColors.AUTOMATIC)) {
            styleConfiguration.setBorderColor(style.borderColor().getIndex());
        }
        if (!style.fillForegroundColor().equals(IndexedColors.AUTOMATIC)) {
            styleConfiguration.setFillForegroundColor(style.fillForegroundColor().getIndex());
        }
        if (!style.fillBackgroundColor().equals(IndexedColors.AUTOMATIC)) {
            styleConfiguration.setFillBackgroundColor(style.fillBackgroundColor().getIndex());
        }
        if (!style.fillPatternType().equals(FillPatternType.NO_FILL)) {
            styleConfiguration.setFillPatternType(style.fillPatternType());
        }
    }

    private void parseFont(Style style, StyleConfiguration styleConfiguration, FontParser fontParser) {
        if (Objects.isNull(styleConfiguration.getFont()) || !styleConfiguration.getFont().isSet()) {
            FontConfiguration fontConfiguration = new FontConfiguration();
            fontParser.parse(style.font(), fontConfiguration);
            styleConfiguration.setFont(fontConfiguration);

        } else {
            fontParser.parse(style.font(), styleConfiguration.getFont());
            styleConfiguration.setFont(styleConfiguration.getFont());
        }
    }

    private void parseLocation(Style style, StyleConfiguration styleConfiguration) {
        if (!style.horizontalAlignment().equals(HorizontalAlignment.GENERAL)) {
            styleConfiguration.setHorizontalAlignment(style.horizontalAlignment());
        }
        if (!style.verticalAlignment().equals(VerticalAlignment.BOTTOM)) {
            styleConfiguration.setVerticalAlignment(style.verticalAlignment());
        }
        if (style.rotation() != -1) {
            styleConfiguration.setRotation(style.rotation());
        }
        if (style.indention() != -1) {
            styleConfiguration.setIndention(style.indention());
        }
    }
}
