package com.cmeza.spring.excel.repository.builders;

import com.cmeza.spring.excel.repository.support.configurations.excel.ExcelConfiguration;
import com.cmeza.spring.excel.repository.support.builders.ExcelGenericBuilder;
import com.cmeza.spring.excel.repository.support.converters.excel.ExcelSheetConverter;
import com.cmeza.spring.excel.repository.support.interceptors.ToExcelInterceptor;
import com.cmeza.spring.excel.repository.support.mappers.ToExcelMapper;
import org.springframework.web.servlet.View;

import java.nio.file.Path;
import java.util.function.Consumer;

public interface ModelToExcelBuilder extends ExcelGenericBuilder<ModelToExcelBuilder> {
    ModelToExcelBuilder withConfiguration(ExcelConfiguration excelConfiguration);

    ModelToExcelBuilder withConfiguration(Consumer<ExcelConfiguration> consumer);

    ExcelSheetConverter sheet();

    ExcelSheetConverter sheet(int index);

    ModelToExcelBuilder withSheet(Consumer<ExcelSheetConverter> sheetConsumer);

    ModelToExcelBuilder withInterceptor(ToExcelInterceptor interceptor);

    ModelToExcelBuilder withMapper(ToExcelMapper mapper);

    ModelToExcelBuilder withPath(Path path);

    ModelToExcelBuilder withFileName(String fileName);

    Path buildFile();

    View buildView();

    ExcelConfiguration getExcelConfiguration();

}
