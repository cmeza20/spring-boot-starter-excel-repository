package com.cmeza.spring.excel.repository.support.annotations.model;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Column {
    @AliasFor("header")
    String value() default "";

    @AliasFor("value")
    String header() default "";

    String mapping() default "";

    boolean ignored() default false;

    String styleName() default "";

    int[] styleRow() default {};
}
