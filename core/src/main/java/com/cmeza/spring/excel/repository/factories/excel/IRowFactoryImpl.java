package com.cmeza.spring.excel.repository.factories.excel;

import com.cmeza.spring.excel.repository.support.factories.excel.generics.IFactory;
import com.cmeza.spring.excel.repository.factories.excel.generics.StylizableFactoryImpl;
import com.cmeza.spring.excel.repository.support.factories.excel.CellFactory;
import com.cmeza.spring.excel.repository.support.factories.excel.HeaderFactory;
import com.cmeza.spring.excel.repository.support.factories.excel.IRowFactory;
import com.cmeza.spring.excel.repository.support.factories.excel.SheetFactory;
import com.cmeza.spring.excel.repository.support.interceptors.ToExcelInterceptor;
import com.cmeza.spring.excel.repository.support.mappers.ToExcelMapper;
import com.cmeza.spring.excel.repository.utils.ExcelUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public class IRowFactoryImpl<T extends IRowFactory<?>> extends StylizableFactoryImpl<T, SheetFactory> implements IRowFactory<T>, IFactory<SXSSFRow, SXSSFSheet> {

    protected final Collection<CellFactory> cells = new LinkedList<>();
    private final int position;
    private int heightMultiplier = 1;

    public IRowFactoryImpl(SheetFactory parent, int position) {
        super(parent);
        Assert.isTrue(position >= 0, "Position must be a positive integer");
        this.position = position;
    }

    @Override
    public T withCell(Object value) {
        cell().withValue(value, (String) value);
        return (T) this;
    }

    @Override
    public T withCell(Object value, String headerAlias) {
        cell().withValue(value, headerAlias);
        return (T) this;
    }

    @Override
    public T withCell(Object value, int position) {
        cell().withValue(value, position);
        return (T) this;
    }

    @Override
    public CellFactory cell() {
        CellFactory cellFactory = new CellFactoryImpl(this);
        cellFactory.withPosition(cells.size());
        this.cells.add(cellFactory);
        return cellFactory;
    }

    @Override
    public T withCell(Consumer<CellFactory> cellFactoryConsumer) {
        Assert.notNull(cellFactoryConsumer, "Consumer must not be null");

        CellFactory cellFactory = new CellFactoryImpl(this);
        cellFactory.withPosition(cells.size());
        cellFactoryConsumer.accept(cellFactory);

        this.cells.add(cellFactory);
        return (T) this;
    }

    @Override
    public T withHeightMultiplier(int multiplier) {
        Assert.isTrue(multiplier > 0, "Multiplier must be a positive integer");
        this.heightMultiplier = multiplier;
        return (T) this;
    }

    @Override
    public SXSSFRow build(SXSSFSheet sheet, ToExcelInterceptor interceptor, ToExcelMapper toExcelMapper) {
        Assert.notNull(sheet, "SXSSFSheet must not be null");
        Assert.notNull(parent, "Parent is null");

        preBuilt(sheet.getWorkbook(), interceptor, toExcelMapper);

        SXSSFRow row = sheet.createRow(position);

        CellStyle parentCellStyle = super.cellStyle;
        if (Objects.isNull(parentCellStyle)) {
            parentCellStyle = parent.findAllStyle(super.alias).orElse(null);
        }

        if (heightMultiplier > 1) {
            row.setHeightInPoints(heightMultiplier * sheet.getDefaultRowHeightInPoints());
        }

        if (Objects.nonNull(interceptor)) {
            interceptor.afterRow(sheet, row, parentCellStyle, row.getRowNum(), (this instanceof HeaderFactory));
        }

        int colPos = 0;
        for (CellFactory cell : cells) {
            if (Objects.nonNull(interceptor)) {
                interceptor.beforeCell(sheet, row, cell, row.getRowNum(), colPos);
            }

            SXSSFCell excelCell = ExcelUtils.build(cell, row, interceptor, toExcelMapper, SXSSFCell.class);
            if (Objects.nonNull(parentCellStyle) && !cell.appliedStyle()) {
                excelCell.setCellStyle(parentCellStyle);
            }
            colPos++;
        }


        return row;
    }
}
