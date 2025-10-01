package com.cmeza.spring.excel.repository.factories.excel;

import com.cmeza.spring.excel.repository.support.exceptions.HeaderNotFoundException;
import com.cmeza.spring.excel.repository.support.exceptions.WorkbookNotFoundException;
import com.cmeza.spring.excel.repository.support.factories.excel.generics.IFactory;
import com.cmeza.spring.excel.repository.factories.excel.generics.StylizableFactoryImpl;
import com.cmeza.spring.excel.repository.factories.excel.generics.StylizablesFactoryImpl;
import com.cmeza.spring.excel.repository.support.interceptors.ToExcelInterceptor;
import com.cmeza.spring.excel.repository.parsers.values.*;
import com.cmeza.spring.excel.repository.support.mappers.ToExcelMapper;
import com.cmeza.spring.excel.repository.support.factories.excel.*;
import com.cmeza.spring.excel.repository.support.parsers.values.ValueParser;
import com.cmeza.spring.excel.repository.utils.ExcelUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class CellFactoryImpl extends StylizableFactoryImpl<CellFactory, IRowFactory<?>> implements CellFactory, IFactory<SXSSFCell, SXSSFRow> {

    private static final Map<Class<?>, ValueParser<?>> valueParsers;

    static {
        valueParsers = new LinkedHashMap<>();
        valueParsers.put(Integer.class, new IntegerValueParser());
        valueParsers.put(Long.class, new LongValueParser());
        valueParsers.put(Double.class, new DoubleValueParser());
        valueParsers.put(Date.class, new DateValueParser());
        valueParsers.put(LocalDate.class, new LocalDateValueParser());
        valueParsers.put(LocalDateTime.class, new LocalDateTimeValueParser());
        valueParsers.put(Boolean.class, new BooleanValueParser());
        valueParsers.put(Object.class, new ObjectValueParser());
        valueParsers.put(String.class, new ObjectValueParser());
    }

    private Object value;
    private String headerAlias;
    private int position;
    private String format;
    private boolean styleApplied;

    public CellFactoryImpl(IRowFactory<?> parent) {
        super(parent);
    }

    @Override
    public CellFactory withValue(Object value, String headerAlias) {
        this.value = value;
        this.headerAlias = headerAlias;
        return this;
    }

    @Override
    public CellFactory withValue(Object value, int position) {
        this.value = value;
        this.position = position;
        return this;
    }

    @Override
    public CellFactory withPosition(int position) {
        this.position = position;
        return this;
    }

    @Override
    public CellFactory withFormat(String format) {
        this.format = format;
        return this;
    }

    @Override
    public boolean appliedStyle() {
        return styleApplied;
    }

    @Override
    public int getPosition() {
        return this.position;
    }

    @Override
    public String getHeaderAlias() {
        return this.headerAlias;
    }

    @Override
    public SXSSFCell build(SXSSFRow row, ToExcelInterceptor interceptor, ToExcelMapper toExcelMapper) {
        Assert.notNull(row, "SXSSFRow is null");
        Assert.notNull(parent, "Parent is null");

        SheetFactory sheetFactory = getParent(SheetFactory.class);

        preBuilt(row.getSheet().getWorkbook(), interceptor, toExcelMapper);

        if (StringUtils.isNotEmpty(headerAlias)) {
            if (Objects.isNull(sheetFactory)) {
                throw new HeaderNotFoundException("Excel header not exists");
            }

            Integer cellPosition = sheetFactory.getHeaderPosition(headerAlias);
            if (Objects.isNull(cellPosition)) {
                throw new HeaderNotFoundException(String.format("Header alias '%s' not found", headerAlias));
            }

            this.position = cellPosition;
        }

        SXSSFCell cell = row.createCell(this.position);
        writeValue(row.getSheet(),row, cell, sheetFactory, interceptor, toExcelMapper);
        return cell;
    }

    public void writeValue(SXSSFSheet sheet, SXSSFRow row, SXSSFCell cell, SheetFactory sheetFactory, ToExcelInterceptor interceptor, ToExcelMapper toExcelMapper) {
        if (Objects.nonNull(toExcelMapper)) {
            value = toExcelMapper.cellValueMapper(sheet.getWorkbook(), sheet, row, cell, value);
        }

        ValueParser<?> valueParser = this.findValueParser(value);

        String stringFormat = null;
        Short formatValue = null;

        if (Objects.nonNull(toExcelMapper)) {
            valueParser = toExcelMapper.cellValueParserMapper(sheet.getWorkbook(), sheet, row, cell, valueParsers, valueParser);
        }

        if (Objects.nonNull(valueParser)) {
            stringFormat = valueParser.getFormat();
            valueParser.apply(value, cell);
        }

        if (Objects.nonNull(sheetFactory)) {

            DataFormat dataFormat = sheetFactory.getDataFormat();
            if (StringUtils.isNotEmpty(format)) {
                formatValue = dataFormat.getFormat(format);
            } else if (StringUtils.isNotEmpty(stringFormat)) {
                formatValue = dataFormat.getFormat(stringFormat);
            }
        }

        applyFormat(sheet, row, cell, formatValue, valueParser, interceptor, toExcelMapper);
    }

    private void applyFormat(SXSSFSheet sheet, SXSSFRow row, SXSSFCell cell, Short format, ValueParser<?> valueParser, ToExcelInterceptor interceptor, ToExcelMapper toExcelMapper) {
        CellStyle parentCellStyle = getParent(SheetFactory.class).findAllStyle(super.alias).orElse(null);

        if (Objects.nonNull(parentCellStyle)) {
            this.applyParentCellStyle(parentCellStyle, format);
            parentCellStyle = this.applyStyleMapper(toExcelMapper, sheet, row, cell, parentCellStyle);
            cell.setCellStyle(parentCellStyle);
            this.styleApplied = true;
        } else if (Objects.nonNull(super.cellStyle)) {
            this.applyCellStyleFormat(super.cellStyle, format);
            super.cellStyle = this.applyStyleMapper(toExcelMapper, sheet, row, cell, super.cellStyle);
            cell.setCellStyle(super.cellStyle);
            this.styleApplied = true;
        } else if (Objects.nonNull(format)) {
            WorkbookFactory workbookFactory = getParent(WorkbookFactory.class);
            if (Objects.isNull(workbookFactory)) {
                throw new WorkbookNotFoundException("WorkbookFactory is null");
            }

            Optional<CellStyle> cellStyleGlobalOptional = workbookFactory.getLocalStyle(format.toString());
            if (cellStyleGlobalOptional.isPresent()) {
                CellStyle cellStyleGlobal = cellStyleGlobalOptional.get();
                cellStyleGlobal = this.applyStyleMapper(toExcelMapper, sheet, row, cell, cellStyleGlobal);
                cell.setCellStyle(cellStyleGlobal);
            } else {
                StyleFactory styleFactory = workbookFactory.style();
                styleFactory.withAlias(format.toString());
                styleFactory.withDataFormat(format);

                workbookFactory.withStyle(styleFactory);
                ((StylizablesFactoryImpl<?, ?>) workbookFactory).preBuilt(cell.getSheet().getWorkbook(), interceptor, toExcelMapper);

                CellStyle cellStyleGeneric = ExcelUtils.build(styleFactory, cell.getSheet().getWorkbook(), interceptor, toExcelMapper, CellStyle.class);
                cellStyleGeneric = this.applyStyleMapper(toExcelMapper, sheet, row, cell, cellStyleGeneric);
                cell.setCellStyle(cellStyleGeneric);
            }
            this.styleApplied = true;
        } else if (Objects.nonNull(toExcelMapper)) {
            CellStyle emptyCellStyle = toExcelMapper.cellStyleMapper(sheet.getWorkbook(), sheet, row, cell, null);
            if (Objects.nonNull(emptyCellStyle)) {
                cell.setCellStyle(emptyCellStyle);
            }
        }

        if (Objects.nonNull(interceptor)) {
            interceptor.afterCell(sheet, row, cell, valueParser, value);
        }
    }

    private ValueParser<?> findValueParser(Object value) {
        return valueParsers.values().stream()
                .filter(c -> c.hasValueMatch(value))
                .findFirst()
                .orElse(valueParsers.get(Object.class));
    }

    private void applyParentCellStyle(CellStyle parentCellStyle, Short format) {
        if (Objects.nonNull(super.cellStyle)) {
            parentCellStyle.cloneStyleFrom(super.cellStyle);
        }

        this.applyCellStyleFormat(parentCellStyle, format);
    }

    private void applyCellStyleFormat(CellStyle parentCellStyle, Short format) {
        if (Objects.nonNull(format)) {
            parentCellStyle.setDataFormat(format);
        }
    }

    private CellStyle applyStyleMapper(ToExcelMapper toExcelMapper, SXSSFSheet sheet, SXSSFRow row, SXSSFCell cell, CellStyle parentCellStyle) {
        if (Objects.nonNull(toExcelMapper)) {
            parentCellStyle = toExcelMapper.cellStyleMapper(sheet.getWorkbook(), sheet, row, cell, parentCellStyle);
        }
        return parentCellStyle;
    }
}
