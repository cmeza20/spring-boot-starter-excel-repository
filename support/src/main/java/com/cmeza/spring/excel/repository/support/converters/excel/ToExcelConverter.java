package com.cmeza.spring.excel.repository.support.converters.excel;

import com.cmeza.spring.excel.repository.support.configurations.excel.ExcelConfiguration;
import com.cmeza.spring.excel.repository.support.interceptors.ToExcelInterceptor;
import com.cmeza.spring.excel.repository.support.mappers.ToExcelMapper;
import org.springframework.web.servlet.View;

import java.nio.file.Path;
import java.util.function.Consumer;

public interface ToExcelConverter {
    ToExcelConverter withConfiguration(ExcelConfiguration excelConfiguration);

    ToExcelConverter withConfiguration(Consumer<ExcelConfiguration> consumer);

    ExcelSheetConverter sheet();

    ExcelSheetConverter sheet(int index);

    ToExcelConverter withSheet(Consumer<ExcelSheetConverter> sheetConsumer);

    ToExcelConverter withPath(Path path);

    ToExcelConverter withFileName(String fileName);

    ToExcelConverter withVersioned(boolean versioned);

    ToExcelConverter withIntercertor(ToExcelInterceptor interceptor);

    ToExcelConverter withMapper(ToExcelMapper mapper);

    Path buildFile();

    View buildView();

    ExcelConfiguration getExcelConfiguration();

    void preBuilt();
}
