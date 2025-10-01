package com.cmeza.spring.excel.repository.support.annotations.support;

import com.cmeza.spring.excel.repository.support.enums.TableStyleValue;

import java.lang.annotation.*;

@Target({})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Table {
    boolean table() default false;

    boolean filter() default false;

    TableStyleValue style() default TableStyleValue.NONE;
}
