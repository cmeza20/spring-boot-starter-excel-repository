package com.cmeza.spring.excel.repository.configurations.beans;

import com.cmeza.spring.excel.repository.support.mappers.ToExcelMapper;
import com.cmeza.spring.excel.repository.support.parsers.values.ValueParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class SimpleToExcelMapper implements ToExcelMapper {

    private static final int ROW_NUM = 1;
    private static final int COL_NUM = 0;
    public static final Double NEW_ID = 9999.9;

    @Override
    public CellStyle cellStyleMapper(SXSSFWorkbook workbook, SXSSFSheet sheet, SXSSFRow row, SXSSFCell cell, CellStyle cellStyle) {
        if (isCellIntercepted(cell)) {
            CellStyle cellStyleNew = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            font.setColor(IndexedColors.BLUE.getIndex());
            cellStyleNew.setFont(font);
            return cellStyleNew;
        }
        return cellStyle;
    }

    @Override
    public ValueParser<?> cellValueParserMapper(SXSSFWorkbook workbook, SXSSFSheet sheet, SXSSFRow row, SXSSFCell cell, Map<Class<?>, ValueParser<?>> valueParsers, ValueParser<?> valueParser) {
        if (isCellIntercepted(cell)) {
            ValueParser<?> valueParserNew = valueParsers.get(String.class);
            log.info("SimpleToExcelMapper :: cellValueParserMapper -> change value parser [{}] to [{}]", valueParser.getValueClass(), valueParserNew.getValueClass());
            return valueParserNew;
        }
        return valueParser;
    }

    @Override
    public Object cellValueMapper(SXSSFWorkbook workbook, SXSSFSheet sheet, SXSSFRow row, SXSSFCell cell, Object value) {
        if (isCellIntercepted(cell)) {
            log.info("SimpleToExcelMapper :: cellValueMapper -> change value [{}] to [{}]", value, NEW_ID);
            return NEW_ID;
        }
        return value;
    }

    private boolean isCellIntercepted(SXSSFCell cell) {
        return cell.getRowIndex() == ROW_NUM && cell.getColumnIndex() == COL_NUM;
    }
}
