package com.cmeza.spring.excel.repository.support.annotations;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Component
public @interface ExcelRepository {
    boolean loggable() default false;

    String dslName() default "";
}
