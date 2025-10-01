package com.cmeza.spring.excel.repository.support.mappers;

import com.cmeza.spring.excel.repository.support.parsers.values.ValueParser;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.util.Map;

public interface ToExcelMapper {
    default CellStyle cellStyleMapper(SXSSFWorkbook workbook, SXSSFSheet sheet, SXSSFRow row, SXSSFCell cell, CellStyle cellStyle) {
        return cellStyle;
    }

    default ValueParser<?> cellValueParserMapper(SXSSFWorkbook workbook, SXSSFSheet sheet, SXSSFRow row, SXSSFCell cell, Map<Class<?>, ValueParser<?>> valueParsers, ValueParser<?> valueParser) {
        return valueParser;
    }

    default Object cellValueMapper(SXSSFWorkbook workbook, SXSSFSheet sheet, SXSSFRow row, SXSSFCell cell, Object value) {
        return value;
    }
}
