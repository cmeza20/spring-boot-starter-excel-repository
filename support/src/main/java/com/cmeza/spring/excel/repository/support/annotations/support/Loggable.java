package com.cmeza.spring.excel.repository.support.annotations.support;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
public @interface Loggable {
    @AliasFor("loggable")
    boolean value() default false;

    @AliasFor("value")
    boolean loggable() default false;
}
