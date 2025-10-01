package com.cmeza.spring.excel.repository.support.factories.excel;

import com.cmeza.spring.excel.repository.support.factories.excel.generics.ParentFactory;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;

public interface StyleFactory extends ParentFactory<ParentFactory<?>> {

    StyleFactory withAlias(String alias);

    StyleFactory withHorizontalAlignment(HorizontalAlignment horizontalAlignment);

    StyleFactory withVerticalAlignment(VerticalAlignment verticalAlignment);

    StyleFactory withFillPatternType(FillPatternType fillPatternType);

    StyleFactory withBorderStyle(BorderStyle borderStyle);

    StyleFactory withBorderColor(Short borderColor);

    StyleFactory withDataFormat(Short dataFormat);

    StyleFactory withHidden(Boolean hidden);

    StyleFactory withLocked(Boolean locked);

    StyleFactory withQuotePrefixed(Boolean quotePrefixed);

    StyleFactory withWrapText(Boolean wrapText);

    StyleFactory withShrinkToFit(Boolean shrinkToFit);

    StyleFactory withRotation(Short rotation);

    StyleFactory withIndention(Short indention);

    StyleFactory withFillForegroundColor(Short fillForegroundColor);

    StyleFactory withFillBackgroundColor(Short fillBackgroundColor);

    StyleFactory withFont(XSSFFont font);

    String getAlias();

}
