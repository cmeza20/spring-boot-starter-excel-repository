package com.cmeza.spring.excel.repository.parsers.excel;

import com.cmeza.spring.excel.repository.support.annotations.support.Font;
import com.cmeza.spring.excel.repository.support.configurations.excel.FontConfiguration;
import com.cmeza.spring.excel.repository.support.parsers.excel.IFontParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.FontFamily;
import org.apache.poi.ss.usermodel.FontUnderline;
import org.apache.poi.ss.usermodel.IndexedColors;

import java.util.Objects;

public class FontParser implements IFontParser<FontConfiguration, Font> {

    @Override
    public void parse(Font font, FontConfiguration fontConfiguration) {
        if (StringUtils.isNotEmpty(font.fontName())) {
            fontConfiguration.setFontName(font.fontName());
        }
        if (font.bold()) {
            fontConfiguration.setBold(true);
        }
        if (font.italic()) {
            fontConfiguration.setItalic(true);
        }
        if (font.strikeout()) {
            fontConfiguration.setStrikeout(true);
        }
        if (font.fontHeight() != -1) {
            fontConfiguration.setFontHeight(font.fontHeight());
        }
        if (font.typeOffset() != -1) {
            fontConfiguration.setTypeOffset(font.typeOffset());
        }
        if (!font.charset().equals(org.apache.poi.common.usermodel.fonts.FontCharset.ANSI)) {
            fontConfiguration.setCharset(font.charset());
        }
        if (!font.color().equals(IndexedColors.AUTOMATIC)) {
            fontConfiguration.setColor(font.color().getIndex());
        }
        if (!font.underline().equals(FontUnderline.NONE)) {
            fontConfiguration.setUnderline(font.underline());
        }
        if (!font.fontFamily().equals(FontFamily.NOT_APPLICABLE)) {
            fontConfiguration.setFontFamily(font.fontFamily());
        }
    }

    @Override
    public void merge(FontConfiguration origin, FontConfiguration target) {
        if (StringUtils.isNotEmpty(origin.getFontName())) {
            target.setFontName(origin.getFontName());
        }
        if (Objects.nonNull(origin.getFontHeight())) {
            target.setFontHeight(origin.getFontHeight());
        }
        if (Objects.nonNull(origin.getFontHeightInPoints())) {
            target.setFontHeightInPoints(origin.getFontHeightInPoints());
        }
        if (Objects.nonNull(origin.getItalic())) {
            target.setItalic(origin.getItalic());
        }
        if (Objects.nonNull(origin.getStrikeout())) {
            target.setStrikeout(origin.getStrikeout());
        }
        if (Objects.nonNull(origin.getBold())) {
            target.setBold(origin.getBold());
        }
        if (Objects.nonNull(origin.getColor())) {
            target.setColor(origin.getColor());
        }
        if (Objects.nonNull(origin.getTypeOffset())) {
            target.setTypeOffset(origin.getTypeOffset());
        }
        if (Objects.nonNull(origin.getUnderline())) {
            target.setUnderline(origin.getUnderline());
        }
        if (Objects.nonNull(origin.getCharset())) {
            target.setCharset(origin.getCharset());
        }
        if (Objects.nonNull(origin.getFontFamily())) {
            target.setFontFamily(origin.getFontFamily());
        }
    }
}
