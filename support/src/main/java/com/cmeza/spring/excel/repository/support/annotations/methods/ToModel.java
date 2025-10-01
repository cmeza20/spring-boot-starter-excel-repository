package com.cmeza.spring.excel.repository.support.annotations.methods;

import com.cmeza.spring.excel.repository.support.annotations.support.Error;
import com.cmeza.spring.excel.repository.support.annotations.support.Loggable;
import com.cmeza.spring.excel.repository.support.annotations.support.Mapping;
import com.cmeza.spring.excel.repository.support.constants.SupportConstants;
import com.cmeza.spring.excel.repository.support.mappers.ToModelMapper;
import com.cmeza.spring.excel.repository.support.maps.MapModel;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
@Loggable
public @interface ToModel {
    @AliasFor(annotation = Loggable.class)
    boolean loggable() default false;

    String modelConfigurationBean() default "";

    boolean hierarchical() default true;

    int sheetIndex() default 0;

    String sheetName() default "";

    int rowCacheSize() default SupportConstants.DEFAULT_ROW_CACHE_SIZE;

    int bufferSize() default SupportConstants.DEFAULT_BUFFER_SIZE;

    Class<? extends ToModelMapper<?>>[] mapper() default {};

    Class<? extends MapModel<?, ?>>[] map() default {};

    Mapping[] mappings() default {};

    Error error() default @Error();

}
