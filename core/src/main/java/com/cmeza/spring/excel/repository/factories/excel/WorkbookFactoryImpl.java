package com.cmeza.spring.excel.repository.factories.excel;

import com.cmeza.spring.excel.repository.support.exceptions.SheetNotFoundException;
import com.cmeza.spring.excel.repository.support.factories.excel.SheetFactory;
import com.cmeza.spring.excel.repository.support.factories.excel.WorkbookFactory;
import com.cmeza.spring.excel.repository.factories.excel.generics.StylizablesFactoryImpl;
import com.cmeza.spring.excel.repository.support.interceptors.ToExcelInterceptor;
import com.cmeza.spring.excel.repository.support.mappers.ToExcelMapper;
import com.cmeza.spring.excel.repository.utils.ExcelUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Consumer;

public class WorkbookFactoryImpl extends StylizablesFactoryImpl<WorkbookFactory, WorkbookFactory> implements WorkbookFactory {
    private final Collection<SheetFactory> sheets = new LinkedList<>();
    private ToExcelInterceptor interceptor;
    private ToExcelMapper mapper;

    public WorkbookFactoryImpl() {
        super(null);
    }

    @Override
    public SheetFactory sheet() {
        SheetFactory sheetFactory = new SheetFactoryImpl(this);
        sheets.add(sheetFactory);
        return sheetFactory;
    }

    @Override
    public WorkbookFactory withSheet(Consumer<SheetFactory> sheetFactoryConsumer) {
        Assert.notNull(sheetFactoryConsumer, "Consumer must not be null");

        SheetFactory sheetFactory = new SheetFactoryImpl(this);
        sheetFactoryConsumer.accept(sheetFactory);

        sheets.add(sheetFactory);
        return this;
    }

    @Override
    public WorkbookFactory withInterceptor(ToExcelInterceptor interceptor) {
        Assert.notNull(interceptor, "Interceptor must not be null");
        this.interceptor = interceptor;
        return this;
    }

    @Override
    public WorkbookFactory withMapper(ToExcelMapper mapper) {
        Assert.notNull(mapper, "ToExcelMapper must not be null");
        this.mapper = mapper;
        return this;
    }

    @Override
    public boolean hasSheets() {
        return !sheets.isEmpty();
    }

    @Override
    public boolean hasStyles() {
        return !styles.isEmpty();
    }

    @Override
    public int sheetCount(String sheetName) {
        return (int) sheets.stream()
                .filter(s -> StringUtils.isNotEmpty(s.getSheetName()) && s.getSheetName().equals(sheetName)).count();
    }

    @Override
    public SXSSFWorkbook build() {
        if (sheets.isEmpty()) {
            throw new SheetNotFoundException("No sheets found");
        }

        SXSSFWorkbook workbook = new SXSSFWorkbook();
        workbook.setCompressTempFiles(true);

        preBuilt(workbook, interceptor, mapper);

        sheets.forEach(sheet -> {
            sheet.withDataFormat(workbook.createDataFormat());
            ExcelUtils.build(sheet, workbook, interceptor, mapper);
        });

        return workbook;
    }
}
