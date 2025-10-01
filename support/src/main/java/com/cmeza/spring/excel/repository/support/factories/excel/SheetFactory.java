package com.cmeza.spring.excel.repository.support.factories.excel;

import com.cmeza.spring.excel.repository.support.enums.TableStyleValue;
import com.cmeza.spring.excel.repository.support.factories.excel.generics.StylizablesFactory;
import org.apache.poi.ss.usermodel.DataFormat;

import java.util.function.Consumer;

public interface SheetFactory extends StylizablesFactory<SheetFactory, WorkbookFactory> {
    SheetFactory withName(String sheetName);

    RowFactory row();

    RowFactory row(int position);

    SheetFactory withColumnWidth(int columnIndex, int width);

    HeaderFactory header();

    SheetFactory withHeader(Consumer<HeaderFactory> headerFactoryConsumer);

    SheetFactory withRow(Consumer<RowFactory> rowFactoryConsumer);

    SheetFactory withRow(Consumer<RowFactory> rowFactoryConsumer, int position);

    SheetFactory withDataFormat(DataFormat dataFormat);

    DataFormat getDataFormat();

    Integer getHeaderPosition(String headerName);

    SheetFactory withTable(boolean withTable);

    SheetFactory withFilter(boolean withFilter);

    SheetFactory withTableStyle(TableStyleValue tableStyleValue);

    String getSheetName();
}
