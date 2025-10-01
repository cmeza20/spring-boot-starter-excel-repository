package com.cmeza.spring.excel.repository.support.annotations.support;

import org.apache.poi.common.usermodel.fonts.FontCharset;
import org.apache.poi.ss.usermodel.FontFamily;
import org.apache.poi.ss.usermodel.FontUnderline;
import org.apache.poi.ss.usermodel.IndexedColors;

import java.lang.annotation.*;

@Target({})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Font {
    String fontName() default "";

    boolean bold() default false;

    boolean italic() default false;

    boolean strikeout() default false;

    short fontHeight() default -1;

    short fontHeightInPoints() default -1;

    short typeOffset() default -1;

    FontCharset charset() default FontCharset.ANSI;

    IndexedColors color() default IndexedColors.AUTOMATIC;

    FontUnderline underline() default FontUnderline.NONE;

    FontFamily fontFamily() default FontFamily.NOT_APPLICABLE;
}
