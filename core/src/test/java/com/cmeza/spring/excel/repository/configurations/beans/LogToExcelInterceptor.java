package com.cmeza.spring.excel.repository.configurations.beans;

import com.cmeza.spring.excel.repository.support.factories.excel.CellFactory;
import com.cmeza.spring.excel.repository.support.factories.excel.HeaderFactory;
import com.cmeza.spring.excel.repository.support.factories.excel.RowFactory;
import com.cmeza.spring.excel.repository.support.interceptors.ToExcelInterceptor;
import com.cmeza.spring.excel.repository.support.parsers.values.ValueParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogToExcelInterceptor implements ToExcelInterceptor {
    @Override
    public void beforeRow(SXSSFSheet sheet, HeaderFactory headerFactory) {
        log.info("beforeRow HEADER :: Sheet: [{}]", sheet.getSheetName());
    }

    @Override
    public void beforeRow(SXSSFSheet sheet, RowFactory rowFactory, int rowPosition) {
        log.info("beforeRow ROW :: Sheet: [{}] Row Position: [{}]", sheet.getSheetName(), rowPosition);
    }

    @Override
    public void afterRow(SXSSFSheet sheet, SXSSFRow row, CellStyle cellStyle, int rowPosition, boolean isHeader) {
        log.info("afterRow :: Sheet: [{}] Row Position: [{}] isHeader: [{}]", sheet.getSheetName(), rowPosition, isHeader);
    }

    @Override
    public void beforeCell(SXSSFSheet sheet, SXSSFRow row, CellFactory cellFactory, int rowPosition, int colPosition) {
        log.info("beforeCell :: Sheet: [{}] Row Position: [{}] Col Position: {}]", sheet.getSheetName(), rowPosition, colPosition);
    }

    @Override
    public <T> void afterCell(SXSSFSheet sheet, SXSSFRow row, SXSSFCell cell, ValueParser<T> valueParser, Object value) {
        log.info("afterCell :: Sheet: [{}] Row Position: [{}] Col Position: [{}] Value: [{}]", sheet.getSheetName(), cell.getRowIndex(), cell.getColumnIndex(), value);
        if (cell.getColumnIndex() != 0 && cell.getRowIndex() != 0) {
            String newValue = "100001";
            log.info("afterCell :: Change Value [{} -> {}]", value, newValue);
            cell.setCellValue(newValue);
        }
    }
}
