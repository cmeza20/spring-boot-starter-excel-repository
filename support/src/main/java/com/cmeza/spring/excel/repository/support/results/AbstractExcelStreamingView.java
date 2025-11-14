package com.cmeza.spring.excel.repository.support.results;

import jakarta.servlet.ServletOutputStream;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import java.io.IOException;
import java.util.Map;

@SuppressWarnings("all")
@RequiredArgsConstructor
public abstract class AbstractExcelStreamingView extends AbstractXlsxStreamingView {
    private final SXSSFWorkbook workbook;

    protected abstract String fileName();

    @Override
    protected SXSSFWorkbook createWorkbook(Map<String, Object> model, jakarta.servlet.http.HttpServletRequest request) {
        return workbook;
    }

    @Override
    protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response) throws Exception {

    }

    @Override
    protected void renderWorkbook(Workbook workbook, jakarta.servlet.http.HttpServletResponse response) throws IOException {
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName() + "\"");

        ServletOutputStream out = response.getOutputStream();
        workbook.write(out);
        workbook.close();

        ((SXSSFWorkbook) workbook).dispose();
    }

}
