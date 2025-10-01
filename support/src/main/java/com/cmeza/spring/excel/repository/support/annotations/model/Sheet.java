package com.cmeza.spring.excel.repository.support.annotations.model;

import com.cmeza.spring.excel.repository.support.annotations.support.Header;
import com.cmeza.spring.excel.repository.support.annotations.support.Mapping;
import com.cmeza.spring.excel.repository.support.annotations.support.Table;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Sheets.class)
@Documented
public @interface Sheet {
    @AliasFor("value")
    String name() default "";

    @AliasFor("name")
    String value() default "";

    boolean autosize() default true;

    Header header() default @Header();

    Table table() default @Table();

    Mapping[] mappings() default {};

}
