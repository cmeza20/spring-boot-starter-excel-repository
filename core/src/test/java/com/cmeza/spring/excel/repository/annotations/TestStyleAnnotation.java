package com.cmeza.spring.excel.repository.annotations;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TestStyleAnnotation {
    String styleAlias() default "";

    boolean bold() default false;

    boolean italic() default false;

    short color() default 0;

    int[] rowPosition() default {};
}
