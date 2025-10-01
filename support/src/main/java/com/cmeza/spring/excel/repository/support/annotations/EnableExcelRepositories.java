package com.cmeza.spring.excel.repository.support.annotations;

import com.cmeza.spring.ioc.handler.annotations.EnableIocHandlers;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Inherited
@EnableIocHandlers(ExcelRepository.class)
public @interface EnableExcelRepositories {

    @AliasFor(annotation = EnableIocHandlers.class)
    String[] basePackages() default {};
}
