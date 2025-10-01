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
    private final Map<String, ToExcelDsl> toExcel;

    /**
     * ToModel DSL
     */
    private final Map<String, ToModelDsl> toModel;

    public DslProperties(Map<String, ToExcelDsl> toExcel,
                         Map<String, ToModelDsl> toModel) {
        this.toExcel = toExcel;
        this.toModel = toModel;
    }

    public ToExcelDsl findToExcelDsl(String name) {
        if (Objects.isNull(toExcel)) {
            return new ToExcelDsl();
        }
        return Optional.ofNullable(toExcel.get(name)).orElse(new ToExcelDsl());
    }

    public ToModelDsl findToModelDsl(String name) {
        if (Objects.isNull(toModel)) {
            return new ToModelDsl();
        }
        return Optional.ofNullable(toModel.get(name)).orElse(new ToModelDsl());
    }
}
