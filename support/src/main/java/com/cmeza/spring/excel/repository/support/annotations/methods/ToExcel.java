package com.cmeza.spring.excel.repository.support.annotations.methods;

import com.cmeza.spring.excel.repository.support.annotations.support.Loggable;
import com.cmeza.spring.excel.repository.support.interceptors.ToExcelInterceptor;
import com.cmeza.spring.excel.repository.support.mappers.ToExcelMapper;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
@Loggable
public @interface ToExcel {
    @AliasFor(annotation = Loggable.class)
    boolean loggable() default false;

    String excelConfigurationBean() default "";

    String path() default "";

    String fileName() default "";

    String prefix() default "";

    boolean versioned() default false;

    Class<? extends ToExcelInterceptor>[] interceptor() default {};

    Class<? extends ToExcelMapper>[] mapper() default {};

}
