package com.cmeza.spring.excel.repository.factories.excel;

import com.cmeza.spring.excel.repository.support.factories.excel.HeaderFactory;
import com.cmeza.spring.excel.repository.support.factories.excel.SheetFactory;
import com.cmeza.spring.excel.repository.support.factories.excel.generics.IFactory;
import com.cmeza.spring.excel.repository.support.interceptors.ToExcelInterceptor;
import com.cmeza.spring.excel.repository.support.mappers.ToExcelMapper;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;

import java.util.HashMap;
import java.util.Map;

public class HeaderFactoryImpl extends IRowFactoryImpl<HeaderFactory> implements HeaderFactory, IFactory<SXSSFRow, SXSSFSheet> {

    private final Map<String, Integer> headers = new HashMap<>();
    private boolean freeze;

    public HeaderFactoryImpl(SheetFactory parent, int position) {
        super(parent, position);
    }

    @Override
    public Integer getHeaderPosition(String headerName) {
        return headers.get(headerName);
    }

    @Override
    public HeaderFactory withFreeze(boolean freeze) {
        this.freeze = freeze;
        return this;
    }

    @Override
    public int getHeaderCount() {
        return headers.size();
    }

    @Override
    public String getHeaderName(int headerIndex) {
        return headers
                .entrySet()
                .stream()
                .filter(entry -> headerIndex == entry.getValue())
                .map(Map.Entry::getKey)
                .findFirst().orElse(null);
    }

    @Override
    public SXSSFRow build(SXSSFSheet sheet, ToExcelInterceptor interceptor, ToExcelMapper toExcelMapper) {
        cells.forEach(cell -> headers.put(cell.getHeaderAlias(), cell.getPosition()));
        if (freeze) {
            sheet.createFreezePane( 0, 1 );
        }
        return super.build(sheet, interceptor, toExcelMapper);
    }

}
