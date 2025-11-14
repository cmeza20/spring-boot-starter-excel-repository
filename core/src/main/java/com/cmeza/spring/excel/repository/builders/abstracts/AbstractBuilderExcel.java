package com.cmeza.spring.excel.repository.builders.abstracts;

import com.cmeza.spring.excel.repository.support.builders.ExcelGenericBuilder;
import com.cmeza.spring.excel.repository.dsl.properties.ExcelRepositoryProperties;
import com.cmeza.spring.excel.repository.support.exceptions.ExcelException;
import com.cmeza.spring.excel.repository.templates.ExcelRepositoryTemplate;
import com.cmeza.spring.excel.repository.utils.HeaderLog;
import com.cmeza.spring.excel.repository.utils.LoggerUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public abstract class AbstractBuilderExcel<T> implements ExcelGenericBuilder<T>, ExcelMetadata {

    public static final Logger log = LoggerFactory.getLogger(AbstractBuilderExcel.class);
    public final Impl impl;
    private final Map<String, Class<?>> parameters = new LinkedHashMap<>();
    protected boolean loggable;
    protected String key;

    protected AbstractBuilderExcel(Impl impl) {
        this.impl = impl;
    }

    @Override
    public T withKey(String key) {
        this.key = key;
        return (T) this;
    }

    @Override
    public T loggable(boolean loggable) {
        if (loggable) {
            this.loggable = loggable;
        }
        return (T) this;
    }

    @Override
    public <R> R execute(Supplier<R> supplier, Object... params) {
        long mill = System.currentTimeMillis();
        Throwable throwable = null;
        R result = null;
        try {
            HeaderLog jdbcHeaderLog = new HeaderLog()
                    .setLogger(log)
                    .setLoggable(loggable)
                    .setClassName(this.getClass().getSimpleName())
                    .setExcelRepositoryTemplateBeanName(impl.getExcelRepositoryTemplateBeanName())
                    .setKey(this.key)
                    .setPrintExtras(this::printExtras);

            LoggerUtils.printHeaderLog(jdbcHeaderLog);

            result = supplier.get();
        } catch (Exception e) {
            throwable = e;
        } finally {
            this.printParameters(log, loggable, parameters);

            LoggerUtils.printResultType(log, loggable, result, mill);
            if (this.printResultValue()) {
                LoggerUtils.printResultValue(log, loggable, result);
            }
            this.printAdditionalParams(log, loggable, params);
            log.info("|");
        }

        if (Objects.nonNull(throwable)) {
            throw new ExcelException(throwable);
        }

        return result;
    }

    @Override
    public T withParameter(String param, Class<?> clazz) {
        this.parameters.put(param, clazz);
        return (T) this;
    }

    @Override
    public T withParameter(String param, Object value) {
        this.parameters.put(param, Objects.isNull(value) ? null : value.getClass());
        return (T) this;
    }

    protected boolean printResultValue() {
        return true;
    }

    protected void printParameters(Logger log, boolean loggable, Map<String, Class<?>> parameters) {
        LoggerUtils.printParametersLog(log, loggable, parameters);
    }

    protected void printAdditionalParams(Logger log, boolean loggable, Object... params) {

    }

    @Getter
    @RequiredArgsConstructor
    public static class Impl {
        private final ExcelRepositoryTemplate excelRepositoryTemplate;
        private final ExcelRepositoryProperties excelRepositoryProperties;
        private final ApplicationContext applicationContext;
        private final String excelRepositoryTemplateBeanName;
    }

}
