package com.cmeza.spring.excel.repository.factories.excel;

import com.cmeza.spring.excel.repository.support.factories.excel.RowFactory;
import com.cmeza.spring.excel.repository.support.factories.excel.SheetFactory;
import com.cmeza.spring.excel.repository.support.factories.excel.generics.IFactory;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;

public class RowFactoryImpl extends IRowFactoryImpl<RowFactory> implements RowFactory, IFactory<SXSSFRow, SXSSFSheet> {

    public RowFactoryImpl(SheetFactory parent, int position) {
        super(parent, position);
    }
}
