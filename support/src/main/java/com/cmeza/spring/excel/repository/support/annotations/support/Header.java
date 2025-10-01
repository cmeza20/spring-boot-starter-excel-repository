package com.cmeza.spring.excel.repository.support.annotations.support;

import java.lang.annotation.*;

@Target({})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Header {
    boolean header() default true;

    boolean freeze() default false;

    String styleName() default "";

    int headerHeightMultiplier() default -1;
}
