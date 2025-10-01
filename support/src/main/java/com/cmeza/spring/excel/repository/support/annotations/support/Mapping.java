package com.cmeza.spring.excel.repository.support.annotations.support;

import java.lang.annotation.*;

@Target({})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Mapping {
    String value();

    String headerName() default "";

    String styleAliasBean() default "";
}
