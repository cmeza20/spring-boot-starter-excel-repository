package com.cmeza.spring.excel.repository.support.annotations.model;

import com.cmeza.spring.excel.repository.support.annotations.support.Font;
import org.apache.poi.ss.usermodel.*;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Styles.class)
@Documented
public @interface Style {
    String name();

    HorizontalAlignment horizontalAlignment() default HorizontalAlignment.GENERAL;

    VerticalAlignment verticalAlignment() default VerticalAlignment.BOTTOM;

    BorderStyle borderStyle() default BorderStyle.NONE;

    FillPatternType fillPatternType() default FillPatternType.NO_FILL;

    boolean hidden() default false;

    boolean locked() default false;

    boolean quotePrefixed() default false;

    boolean wrapText() default false;

    boolean shrinkToFit() default false;

    short dataFormat() default -1;

    short rotation() default -1;

    short indention() default -1;

    IndexedColors borderColor() default IndexedColors.AUTOMATIC;

    IndexedColors fillForegroundColor() default IndexedColors.AUTOMATIC;

    IndexedColors fillBackgroundColor() default IndexedColors.AUTOMATIC;

    Font font() default @Font();
}
