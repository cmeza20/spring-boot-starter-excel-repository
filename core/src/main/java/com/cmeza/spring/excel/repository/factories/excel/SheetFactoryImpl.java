package com.cmeza.spring.excel.repository.factories.excel;

import com.cmeza.spring.excel.repository.support.factories.excel.HeaderFactory;
import com.cmeza.spring.excel.repository.support.factories.excel.RowFactory;
import com.cmeza.spring.excel.repository.support.factories.excel.SheetFactory;
import com.cmeza.spring.excel.repository.support.factories.excel.WorkbookFactory;
import com.cmeza.spring.excel.repository.support.factories.excel.generics.IFactory;
import com.cmeza.spring.excel.repository.factories.excel.generics.StylizablesFactoryImpl;
import com.cmeza.spring.excel.repository.support.interceptors.ToExcelInterceptor;
import com.cmeza.spring.excel.repository.support.enums.TableStyleValue;
import com.cmeza.spring.excel.repository.support.mappers.ToExcelMapper;
import com.cmeza.spring.excel.repository.utils.ExcelUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumns;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableStyleInfo;
import org.springframework.util.Assert;

import java.util.*;
import java.util.function.Consumer;

public class SheetFactoryImpl extends StylizablesFactoryImpl<SheetFactory, WorkbookFactory> implements SheetFactory, IFactory<SXSSFSheet, SXSSFWorkbook> {
    private final Collection<RowFactory> rows = new LinkedList<>();
    private final Map<Integer, Integer> columnWidths = new LinkedHashMap<>();
    private String name;
    private HeaderFactory header;
    private DataFormat dataFormat;
    private boolean table;
    private boolean filter;
    private TableStyleValue tableStyleValue;

    public SheetFactoryImpl(WorkbookFactory parent) {
        super(parent);
    }

    @Override
    public SheetFactory withName(String sheetName) {
        this.name = sheetName;
        return this;
    }

    @Override
    public HeaderFactory header() {
        HeaderFactory headerFactory = new HeaderFactoryImpl(this, 0);
        this.header = headerFactory;
        return headerFactory;
    }

    @Override
    public RowFactory row() {
        return row(calculateRowPosition());
    }

    @Override
    public RowFactory row(int position) {
        RowFactory rowFactory = new RowFactoryImpl(this, position);
        this.rows.add(rowFactory);
        return rowFactory;
    }

    @Override
    public SheetFactory withColumnWidth(int columnIndex, int width) {
        this.columnWidths.put(columnIndex, width);
        return this;
    }

    @Override
    public SheetFactory withHeader(Consumer<HeaderFactory> headerFactoryConsumer) {
        Assert.notNull(headerFactoryConsumer, "Consumer must not be null");

        HeaderFactory headerFactory = new HeaderFactoryImpl(this, 0);
        headerFactoryConsumer.accept(headerFactory);

        this.header = headerFactory;
        return this;
    }

    @Override
    public SheetFactory withRow(Consumer<RowFactory> rowFactoryConsumer) {
        return withRow(rowFactoryConsumer, calculateRowPosition());
    }

    @Override
    public SheetFactory withRow(Consumer<RowFactory> rowFactoryConsumer, int position) {
        Assert.notNull(rowFactoryConsumer, "Consumer must not be null");
        Assert.isTrue(position >= 0, "Position must not be negative");

        RowFactory rowFactory = new RowFactoryImpl(this, position);
        rowFactoryConsumer.accept(rowFactory);

        this.rows.add(rowFactory);
        return this;
    }

    @Override
    public SheetFactory withDataFormat(DataFormat dataFormat) {
        Assert.notNull(dataFormat, "DataFormat must not be null");
        this.dataFormat = dataFormat;
        return this;
    }

    @Override
    public DataFormat getDataFormat() {
        return dataFormat;
    }

    @Override
    public Integer getHeaderPosition(String headerName) {
        if (Objects.nonNull(header)) {
            return header.getHeaderPosition(headerName);
        }
        return 0;
    }

    @Override
    public SheetFactory withTable(boolean withTable) {
        this.table = withTable;
        return this;
    }

    @Override
    public SheetFactory withFilter(boolean withFilter) {
        this.filter = withFilter;
        return this;
    }

    @Override
    public SheetFactory withTableStyle(TableStyleValue tableStyleValue) {
        this.tableStyleValue = tableStyleValue;
        return this;
    }

    @Override
    public String getSheetName() {
        return this.name;
    }

    @Override
    public SXSSFSheet build(SXSSFWorkbook workbook, ToExcelInterceptor interceptor, ToExcelMapper toExcelMapper) {
        Assert.notNull(workbook, "Workbook must not be null");

        preBuilt(workbook, interceptor, toExcelMapper);

        SXSSFSheet sheet;
        if (StringUtils.isEmpty(name)) {
            sheet = workbook.createSheet();
        } else {
            sheet = workbook.createSheet(name);
        }
        sheet.setRandomAccessWindowSize(SXSSFWorkbook.DEFAULT_WINDOW_SIZE);

        int[] rowPos = {0};
        this.buildHeader(interceptor, toExcelMapper, sheet, rowPos);
        this.buildRows(interceptor, toExcelMapper, sheet, rowPos);

        columnWidths.forEach(sheet::setColumnWidth);


        if (Objects.nonNull(header) && table) {

            CellReference topLeft = new CellReference(0, 0);
            CellReference bottomRight = new CellReference(rows.size(), header.getHeaderCount() - 1);
            XSSFWorkbook xssfWorkbook = workbook.getXSSFWorkbook();

            AreaReference tableArea = new AreaReference(topLeft, bottomRight, SpreadsheetVersion.EXCEL2007);

            XSSFSheet xssfSheet = xssfWorkbook.getSheet(sheet.getSheetName());
            XSSFTable dataTable = xssfSheet.createTable(tableArea);

            CTTable cttable = dataTable.getCTTable();

            CTTableColumns columns = cttable.getTableColumns();

            for (int c = 0; c < header.getHeaderCount(); c++) {
                columns.getTableColumnArray(c).setName(header.getHeaderName(c));
            }

            if (Objects.nonNull(tableStyleValue) && !tableStyleValue.equals(TableStyleValue.NONE)) {
                CTTableStyleInfo ctTableStyleInfo = cttable.addNewTableStyleInfo();
                ctTableStyleInfo.setName(tableStyleValue.getValue());
                ctTableStyleInfo.setShowRowStripes(true);
                ctTableStyleInfo.setShowColumnStripes(false);
            }

            if (filter) {
                cttable.addNewAutoFilter().setRef(tableArea.formatAsString());
            }
        }

        return sheet;
    }

    private int calculateRowPosition() {
        if (Objects.nonNull(header)) {
            return rows.size() + 1;
        }
        return rows.size();
    }

    private void buildHeader(ToExcelInterceptor interceptor, ToExcelMapper toExcelMapper, SXSSFSheet sheet, int[] rowPos) {
        if (Objects.nonNull(header)) {
            if (Objects.nonNull(interceptor)) {
                interceptor.beforeRow(sheet, header);
            }
            ExcelUtils.build(header, sheet, interceptor, toExcelMapper);
            rowPos[0]++;
        }
    }

    private void buildRows(ToExcelInterceptor interceptor, ToExcelMapper toExcelMapper, SXSSFSheet sheet, int[] rowPos) {
        for (RowFactory row : rows) {
            if (Objects.nonNull(interceptor)) {
                interceptor.beforeRow(sheet, row, rowPos[0]);
            }
            ExcelUtils.build(row, sheet, interceptor, toExcelMapper);
            rowPos[0]++;
        }
    }
}
