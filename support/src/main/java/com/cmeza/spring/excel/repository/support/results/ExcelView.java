package com.cmeza.spring.excel.repository.support.results;

import com.cmeza.spring.excel.repository.support.utils.SupportUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

public class ExcelView extends AbstractExcelStreamingView {

    private final String fileName;
    private final String prefix;

    public ExcelView(SXSSFWorkbook workbook, String fileName) {
        super(workbook);
        this.fileName = fileName;
        this.prefix = null;
    }

    public ExcelView(SXSSFWorkbook workbook, String fileName, String prefix) {
        super(workbook);
        this.fileName = fileName;
        this.prefix = prefix;
    }

    @Override
    protected String fileName() {
        if (StringUtils.isNotEmpty(fileName)) {
            return (StringUtils.isNotEmpty(prefix) ? prefix : "") + fileName;
        }

        return SupportUtils.generateDefaultFileName(prefix);
    }

}
