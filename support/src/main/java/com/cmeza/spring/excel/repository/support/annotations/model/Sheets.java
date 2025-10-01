package com.cmeza.spring.excel.repository.support.annotations.model;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Sheets {
    Sheet[] value();
}
