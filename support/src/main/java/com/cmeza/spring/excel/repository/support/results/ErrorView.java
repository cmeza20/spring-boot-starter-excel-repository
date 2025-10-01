package com.cmeza.spring.excel.repository.support.results;

import jakarta.servlet.ServletOutputStream;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.web.servlet.view.document.AbstractXlsView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Map;

@SuppressWarnings("all")
public class ErrorView extends AbstractXlsView {

    private final File file;
    private final Workbook workbook;

    public ErrorView(Workbook workbook, File file) {
        this.workbook = workbook;
        this.file = file;
        this.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    @Override
    protected Workbook createWorkbook(Map<String, Object> model, HttpServletRequest request) {
        return workbook;
    }

    protected Workbook createWorkbook(Map<String, Object> model, jakarta.servlet.http.HttpServletRequest request) {
        return workbook;
    }

    @Override
    protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
        //ignore
    }

    protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response) throws Exception {
        //ignore
    }

    @Override
    protected void renderWorkbook(Workbook workbook, HttpServletResponse response) throws IOException {
        super.renderWorkbook(workbook, response);
    }

    @SuppressWarnings("deprecation")
    protected void renderWorkbook(Workbook workbook, jakarta.servlet.http.HttpServletResponse response) throws IOException {
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

        ServletOutputStream out = response.getOutputStream();
        workbook.write(out);
        workbook.close();

        ((SXSSFWorkbook) workbook).dispose();
    }
}
