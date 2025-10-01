package com.cmeza.spring.excel.repository.annotations;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TestFieldAnnotation {
    String fieldName() default "";

    String header() default "";

    String headerStyle() default "";
}
