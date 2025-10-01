package com.cmeza.spring.excel.repository.aware;

import com.cmeza.spring.excel.repository.resolvers.ExcelPropertyResolver;
import com.cmeza.spring.excel.repository.templates.ExcelRepositoryTemplate;
import org.springframework.beans.factory.Aware;

public interface ExcelRepositoryAware extends Aware {
    default String getQualifier() {
        return "excelRepositoryTemplate";
    }

    void setExcelRepositoryTemplate(ExcelRepositoryTemplate excelRepositoryTemplate);

    void setPropertiesResolver(ExcelPropertyResolver propertiesResolver);
}
