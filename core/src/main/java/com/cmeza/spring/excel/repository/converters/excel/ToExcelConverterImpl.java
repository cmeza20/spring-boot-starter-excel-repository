package com.cmeza.spring.excel.repository.converters.excel;

import com.cmeza.spring.excel.repository.support.converters.excel.ExcelSheetConverter;
import com.cmeza.spring.excel.repository.support.converters.excel.ToExcelConverter;
import com.cmeza.spring.excel.repository.factories.Factory;
import com.cmeza.spring.excel.repository.parsers.excel.ExcelParser;
import com.cmeza.spring.excel.repository.support.configurations.excel.ExcelConfiguration;
import com.cmeza.spring.excel.repository.support.configurations.excel.StyleConfiguration;
import com.cmeza.spring.excel.repository.support.converters.Build;
import com.cmeza.spring.excel.repository.support.exceptions.ExcelException;
import com.cmeza.spring.excel.repository.support.factories.excel.StyleFactory;
import com.cmeza.spring.excel.repository.support.factories.excel.WorkbookFactory;
import com.cmeza.spring.excel.repository.support.interceptors.ToExcelInterceptor;
import com.cmeza.spring.excel.repository.support.mappers.ToExcelMapper;
import com.cmeza.spring.excel.repository.support.parsers.Parser;
import com.cmeza.spring.excel.repository.support.results.ExcelView;
import com.cmeza.spring.excel.repository.utils.ExcelUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.util.Assert;
import org.springframework.web.servlet.View;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@Slf4j
public class ToExcelConverterImpl implements ToExcelConverter {
    private static final Parser PARSER = Parser.getInstance();
    private final List<ExcelSheetConverter> sheetConverters = new LinkedList<>();
    private final List<Consumer<ExcelConfiguration>> excelConfigurationConsumers = new LinkedList<>();
    private final WorkbookFactory workbookFactory;
    private final ExcelConfiguration excelConfiguration;
    private boolean preBuilded;

    public ToExcelConverterImpl() {
        this.workbookFactory = Factory.getWorkbookFactory();
        this.excelConfiguration = new ExcelConfiguration();
    }

    @Override
    public ToExcelConverter withConfiguration(ExcelConfiguration excelConfiguration) {
        Assert.notNull(excelConfiguration, "ExcelConfiguration must not be null");
        PARSER.getParser(ExcelParser.class).merge(excelConfiguration, this.excelConfiguration);
        return this;
    }

    @Override
    public ToExcelConverter withConfiguration(Consumer<ExcelConfiguration> consumer) {
        Assert.notNull(consumer, "Consumer must not be null");
        excelConfigurationConsumers.add(consumer);
        return this;
    }

    @Override
    public ExcelSheetConverter sheet() {
        ExcelSheetConverterImpl<Annotation> excelSheetConverter = new ExcelSheetConverterImpl<>(this, workbookFactory.sheet());
        sheetConverters.add(excelSheetConverter);
        return excelSheetConverter;
    }

    @Override
    public ExcelSheetConverter sheet(int index) {
        Assert.isTrue(index >= 0, "Sheet Index out of bounds");
        if (sheetConverters.isEmpty() || index > sheetConverters.size() - 1) {
            return this.sheet();
        }

        ExcelSheetConverter excelSheetConverter = sheetConverters.get(index);
        if (Objects.nonNull(excelSheetConverter)) {
            return excelSheetConverter;
        }
        return sheetConverters.get(index);
    }

    @Override
    public ToExcelConverter withSheet(Consumer<ExcelSheetConverter> sheetConsumer) {
        Assert.notNull(sheetConsumer, "SheetConsumer must not be null");
        ExcelSheetConverterImpl<Annotation> excelSheetConverter = new ExcelSheetConverterImpl<>(this, workbookFactory.sheet());
        sheetConsumer.accept(excelSheetConverter);
        sheetConverters.add(excelSheetConverter);
        return this;
    }

    @Override
    public ToExcelConverter withPath(Path path) {
        this.excelConfiguration.setPath(path);
        this.excelConfiguration.validate();
        return this;
    }

    @Override
    public ToExcelConverter withFileName(String fileName) {
        this.excelConfiguration.setFileName(fileName);
        this.excelConfiguration.validate();
        return this;
    }

    @Override
    public ToExcelConverter withVersioned(boolean versioned) {
        this.excelConfiguration.setVersioned(versioned);
        return this;
    }

    @Override
    public ToExcelConverter withIntercertor(ToExcelInterceptor interceptor) {
        workbookFactory.withInterceptor(interceptor);
        return this;
    }

    @Override
    public ToExcelConverter withMapper(ToExcelMapper mapper) {
        workbookFactory.withMapper(mapper);
        return this;
    }

    @Override
    public Path buildFile() {
        Path resultPath = this.excelConfiguration.getFullPath();
        try (FileOutputStream outputStream = new FileOutputStream(resultPath.toFile());
             SXSSFWorkbook workbook = buildWorkbook()) {
            workbook.write(outputStream);
        } catch (IOException e) {
            throw new ExcelException(e);
        }

        return resultPath;
    }

    @Override
    public View buildView() {
        this.excelConfiguration.validate();
        return new ExcelView(buildWorkbook(), this.excelConfiguration.getFileName(), excelConfiguration.getPrefix());
    }

    @Override
    public ExcelConfiguration getExcelConfiguration() {
        return this.excelConfiguration;
    }

    @Override
    public void preBuilt() {
        if (!preBuilded) {
            preBuilded = true;
            excelConfigurationConsumers.forEach(consumer -> consumer.accept(excelConfiguration));

            for (StyleConfiguration styleConfiguration : excelConfiguration.getStyles()) {
                if (styleConfiguration.hasAlias() && styleConfiguration.isSet()) {
                    log.trace("Configure Workbook style: {}", styleConfiguration.getAlias());

                    StyleFactory styleFactory = workbookFactory.style();
                    ExcelUtils.styleFactoryApply(styleFactory, styleConfiguration);
                }
            }
        }
    }

    private SXSSFWorkbook buildWorkbook() {
        if (!preBuilded) {
            preBuilt();
        }

        sheetConverters.forEach(sheet -> ((Build) sheet).build());
        return workbookFactory.build();
    }
}
