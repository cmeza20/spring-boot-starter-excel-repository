package com.cmeza.spring.excel.repository.support.factories.excel;

import com.cmeza.spring.excel.repository.support.factories.excel.generics.StylizablesFactory;
import com.cmeza.spring.excel.repository.support.interceptors.ToExcelInterceptor;
import com.cmeza.spring.excel.repository.support.mappers.ToExcelMapper;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.util.function.Consumer;

public interface WorkbookFactory extends StylizablesFactory<WorkbookFactory, WorkbookFactory> {

    SheetFactory sheet();

    WorkbookFactory withSheet(Consumer<SheetFactory> sheetFactoryConsumer);

    WorkbookFactory withInterceptor(ToExcelInterceptor interceptor);

    WorkbookFactory withMapper(ToExcelMapper mapper);

    boolean hasSheets();

    boolean hasStyles();

    int sheetCount(String sheetName);

    SXSSFWorkbook build();
}
