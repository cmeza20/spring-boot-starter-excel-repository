package com.cmeza.spring.excel.repository.interceptors;

import com.cmeza.spring.excel.repository.support.factories.excel.CellFactory;
import com.cmeza.spring.excel.repository.support.factories.excel.HeaderFactory;
import com.cmeza.spring.excel.repository.support.factories.excel.RowFactory;
import com.cmeza.spring.excel.repository.support.interceptors.ToExcelInterceptor;
import com.cmeza.spring.excel.repository.support.parsers.values.ValueParser;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;

public class DefaultToExcelInterceptor implements ToExcelInterceptor {
    @Override
    public void beforeRow(SXSSFSheet sheet, HeaderFactory headerFactory) {
        //Not implemented
    }

    @Override
    public void beforeRow(SXSSFSheet sheet, RowFactory rowFactory, int rowPosition) {
        //Not implemented
    }

    @Override
    public void afterRow(SXSSFSheet sheet, SXSSFRow row, CellStyle cellStyle, int rowPosition, boolean isHeader) {
        //Not implemented
    }

    @Override
    public void beforeCell(SXSSFSheet sheet, SXSSFRow row, CellFactory cellFactory, int rowPosition, int colPosition) {
        //Not implemented
    }

    @Override
    public <T> void afterCell(SXSSFSheet sheet, SXSSFRow row, SXSSFCell cell, ValueParser<T> valueParser, Object value) {
        //Not implemented
    }
}
