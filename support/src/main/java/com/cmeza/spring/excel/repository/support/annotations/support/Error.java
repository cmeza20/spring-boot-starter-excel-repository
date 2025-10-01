package com.cmeza.spring.excel.repository.support.annotations.support;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Error {
    String fileName() default "";

    String folder() default "";

    boolean versioned() default false;
}
