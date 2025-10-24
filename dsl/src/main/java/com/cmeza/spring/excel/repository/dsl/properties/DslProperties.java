package com.cmeza.spring.excel.repository.dsl.properties;

import com.cmeza.spring.excel.repository.dsl.models.ToExcelDsl;
import com.cmeza.spring.excel.repository.dsl.models.ToModelDsl;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Data
@Validated
@ConfigurationProperties("spring.excel.repository.dsl")
public class DslProperties {
    /**
     * ToExcel DSL
     */
    private final Map<String, Map<String, ToExcelDsl>> toExcel;

    /**
     * ToModel DSL
     */
    private final Map<String, Map<String, ToModelDsl>> toModel;

    public DslProperties(Map<String, Map<String, ToExcelDsl>> toExcel,
                         Map<String, Map<String, ToModelDsl>> toModel) {
        this.toExcel = toExcel;
        this.toModel = toModel;
    }

    public ToExcelDsl findToExcelDsl(String className, String methodName) {
        if (Objects.isNull(toExcel)) {
            return new ToExcelDsl();
        }
        Map<String, ToExcelDsl> methods = toExcel.get(className);
        if (Objects.isNull(methods)) {
            return new ToExcelDsl();
        }

        return Optional.ofNullable(methods.get(methodName)).orElse(new ToExcelDsl());
    }

    public ToModelDsl findToModelDsl(String className, String methodName) {
        if (Objects.isNull(toModel)) {
            return new ToModelDsl();
        }
        Map<String, ToModelDsl> methods = toModel.get(className);
        if (Objects.isNull(methods)) {
            return new ToModelDsl();
        }
        return Optional.ofNullable(methods.get(methodName)).orElse(new ToModelDsl());
    }
}
