package com.cmeza.spring.excel.repository.tests.units;

import com.cmeza.spring.excel.repository.factories.Factory;
import com.cmeza.spring.excel.repository.support.factories.excel.*;
import com.cmeza.spring.excel.repository.support.exceptions.SheetNotFoundException;
import com.cmeza.spring.excel.repository.support.interceptors.ToExcelInterceptor;
import com.cmeza.spring.excel.repository.support.factories.excel.WorkbookFactory;
import com.cmeza.spring.excel.repository.support.parsers.values.ValueParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

@Slf4j
class ExcelFactoryTest {

    private static Function<WorkbookFactory, HeaderFactory> headerFunction;

    @BeforeAll
    static void setup() {
        log.info("Setup FactoriesTest");

        headerFunction = workbookFactory -> {
            //Sheet
            SheetFactory sheetFactory = workbookFactory.sheet();

            //Header
            HeaderFactory headerFactory = sheetFactory.header();
            Assertions.assertNotNull(headerFactory, "HeaderFactory is null");

            //Set Headers
            headerFactory.withCell("H1");
            headerFactory.withCell("H2");
            headerFactory.withCell("H3");
            headerFactory.withCell("H4", "H4Alias");
            headerFactory.withCell("H5", "H5Alias");
            headerFactory.withCell(cell -> cell.withValue("H6", "H6Alias"));
            CellFactory cell = headerFactory.cell();
            cell.withValue("H7", "H7Alias");

            return headerFactory;
        };
    }

    @Test
    void testWorkbookFactory() {
        log.info("Init testWorkbookFactory");

        //Workbook
        WorkbookFactory workbookFactory = Factory.getWorkbookFactory();
        Assertions.assertNotNull(workbookFactory, "WorkbookFactory is null");
    }

    @Test
    void testEmptySheetFactory() {
        log.info("Init testEmptySheetFactory");

        //Workbook
        WorkbookFactory workbookFactory = Factory.getWorkbookFactory();
        Assertions.assertThrowsExactly(SheetNotFoundException.class, workbookFactory::build);
    }

    @Test
    void testOneSheetFactory() {
        log.info("Init testOneSheetFactory");

        //Vars
        final String sheetName = "Mi Sheet 1";

        //Workbook
        WorkbookFactory workbookFactory = Factory.getWorkbookFactory();

        //Sheet
        workbookFactory.withSheet(sheet -> sheet.withName(sheetName));

        //Build validate
        SXSSFWorkbook workbook = workbookFactory.build();
        Assertions.assertNotNull(workbook, "SXSSFWorkbook is null");

        //Sheet validate
        SXSSFSheet sheet1 = workbook.getSheetAt(0);
        Assertions.assertEquals(sheet1.getSheetName(), sheetName, "Sheet name is not equal to " + sheetName);
    }

    @Test
    void testHeaderFactory() {
        log.info("Init testHeaderFactory");

        //Workbook
        WorkbookFactory workbookFactory = Factory.getWorkbookFactory();

        //Header
        headerFunction.apply(workbookFactory);

        //Values Validation
        SXSSFWorkbook workbook = workbookFactory.build();
        SXSSFSheet sheet = workbook.getSheetAt(0);
        SXSSFRow row = sheet.getRow(0);
        SXSSFCell cellH1 = row.getCell(0);
        SXSSFCell cellH5 = row.getCell(4);
        SXSSFCell cellH6 = row.getCell(5);

        Assertions.assertEquals("H1", cellH1.getStringCellValue(), "Header name is not equal to H1");
        Assertions.assertEquals("H5", cellH5.getStringCellValue(), "Header name is not equal to H5");
        Assertions.assertEquals("H6", cellH6.getStringCellValue(), "Header name is not equal to H6");
    }

    @Test
    void testRowFactory() {
        log.info("Init testRowFactory");

        //Workbook
        WorkbookFactory workbookFactory = Factory.getWorkbookFactory();

        //Header
        HeaderFactory headerFactory = headerFunction.apply(workbookFactory);

        //Sheet
        SheetFactory sheetFactory = headerFactory.getParent();

        //Row
        RowFactory row1 = sheetFactory.row(1);

        //Cell by position
        row1.withCell("H2 value", 1);

        //Cell by alias
        row1.withCell("H6 value", "H6Alias");

        //Values Validation
        SXSSFWorkbook workbook = workbookFactory.build();
        SXSSFSheet sheet = workbook.getSheetAt(0);
        SXSSFRow row = sheet.getRow(1);
        SXSSFCell cellH2 = row.getCell(1);
        SXSSFCell cellH6 = row.getCell(5);

        Assertions.assertEquals("H2 value", cellH2.getStringCellValue(), "Cell value is not equal to H2 value");
        Assertions.assertEquals("H6 value", cellH6.getStringCellValue(), "Cell value is not equal to H6 value");
    }

    @Test
    void testHeaderStyleFactory() {
        log.info("Init testHeaderStyleFactory");

        //Workbook
        WorkbookFactory workbookFactory = Factory.getWorkbookFactory();

        //Header
        HeaderFactory headerFactory = headerFunction.apply(workbookFactory);

        //Header Style
        headerFactory.withStyle(styleFactory -> styleFactory.withAlias("HeaderStyle")
                .withVerticalAlignment(VerticalAlignment.CENTER)
                .withHorizontalAlignment(HorizontalAlignment.CENTER)
                .withBorderStyle(BorderStyle.DASH_DOT));

        //Values Validation
        SXSSFWorkbook workbook = workbookFactory.build();
        SXSSFSheet sheet = workbook.getSheetAt(0);
        SXSSFRow row = sheet.getRow(0);
        SXSSFCell cell = row.getCell(3);
        CellStyle cellStyle = cell.getCellStyle();

        Assertions.assertEquals(VerticalAlignment.CENTER, cellStyle.getVerticalAlignment(), "VerticalAlignment is not equal to CENTER");
        Assertions.assertEquals(HorizontalAlignment.CENTER, cellStyle.getAlignment(), "HorizontalAlignment is not equal to CENTER");
        Assertions.assertEquals(BorderStyle.DASH_DOT, cellStyle.getBorderTop(), "BorderStyle is not equal to DASH_DOT");
    }

    @Test
    void testGlobalStyleFactory() {
        log.info("Init testGlobalStyleFactory");

        //Vars
        String globalStyleName = "GlobalStyle";

        //Workbook
        WorkbookFactory workbookFactory = Factory.getWorkbookFactory();

        //Header
        HeaderFactory headerFactory = headerFunction.apply(workbookFactory);

        //Global Style
        XSSFFont font = new XSSFFont();
        font.setBold(true);
        font.setItalic(true);
        font.setFamily(FontFamily.MODERN);
        workbookFactory.withStyle(styleFactory -> styleFactory.withAlias(globalStyleName).withFont(font));

        //Cell
        RowFactory row1 = headerFactory.getParent().row(1);
        row1.withStyleAlias(globalStyleName);

        CellFactory cell1 = row1.cell();
        cell1.withValue("H7 value", 6);
        cell1.withStyleAlias(globalStyleName);

        //Values Validation
        SXSSFWorkbook workbook = workbookFactory.build();
        SXSSFSheet sheet = workbook.getSheetAt(0);
        SXSSFRow row = sheet.getRow(1);
        SXSSFCell cell = row.getCell(6);
        XSSFCellStyle cellStyle = (XSSFCellStyle) cell.getCellStyle();
        XSSFFont finalFont = cellStyle.getFont();

        Assertions.assertTrue(finalFont.getBold(), "Font.bold is not equal to TRUE");
        Assertions.assertTrue(finalFont.getItalic(), "Font.italic is not equal to TRUE");
        Assertions.assertEquals(FontFamily.MODERN.getValue(), finalFont.getFamily(), "Font.family is not equal to MODERN");
    }

    @Test
    void testInterceptors() throws ExecutionException, InterruptedException {
        log.info("Init testInterceptors");

        //Workbook
        WorkbookFactory workbookFactory = Factory.getWorkbookFactory();

        //Header
        HeaderFactory headerFactory = headerFunction.apply(workbookFactory);

        //Sheet
        SheetFactory sheetFactory = headerFactory.getParent();

        //Row
        RowFactory row1 = sheetFactory.row(1);
        row1.withCell("C1", 1);
        row1.withCell("C2", 1);
        row1.withCell("C3", 1);
        row1.withCell("C4", 1);
        row1.withCell("C5", 1);
        row1.withCell("C6", 1);
        row1.withCell("C7", 1);

        final List<String> messages = new ArrayList<>();

        //Interceptor
        workbookFactory.withInterceptor(new ToExcelInterceptor() {
            @Override
            public void beforeRow(SXSSFSheet sheet, HeaderFactory headerFactory) {
                messages.add(String.format("beforeRow HEADER :: Sheet: [%s]", sheet.getSheetName()));
            }

            @Override
            public void beforeRow(SXSSFSheet sheet, RowFactory rowFactory, int rowPosition) {
                messages.add(String.format("beforeRow ROW :: Sheet: [%s] Row Position: [%s]", sheet.getSheetName(), rowPosition));
            }

            @Override
            public void afterRow(SXSSFSheet sheet, SXSSFRow row, CellStyle cellStyle, int rowPosition, boolean isHeader) {
                messages.add(String.format("afterRow :: Sheet: [%s] Row Position: [%s] isHeader: [%s]", sheet.getSheetName(), rowPosition, isHeader));
            }

            @Override
            public void beforeCell(SXSSFSheet sheet, SXSSFRow row, CellFactory cellFactory, int rowPosition, int colPosition) {
                messages.add(String.format("beforeCell :: Sheet: [%s] Row Position: [%s] Col Position: [%s]", sheet.getSheetName(), rowPosition, colPosition));
            }

            @Override
            public <T> void afterCell(SXSSFSheet sheet, SXSSFRow row, SXSSFCell cell, ValueParser<T> valueParser, Object value) {
                messages.add(String.format("afterCell :: Sheet: [%s] Row Position: [%s] Col Position: [%s] Value: [%s]", sheet.getSheetName(), cell.getRowIndex(), cell.getColumnIndex(), value));
            }
        });

        //Validations
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            workbookFactory.build();
            messages.forEach(log::info);
            return messages.size();
        });

        Assertions.assertEquals(32, future.get(), "Number of results is incorrect");
    }

}
