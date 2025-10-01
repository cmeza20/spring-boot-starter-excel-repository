package com.cmeza.spring.excel.repository.support.factories.excel;

public interface HeaderFactory extends IRowFactory<HeaderFactory> {

    Integer getHeaderPosition(String headerName);

    HeaderFactory withFreeze(boolean freeze);

    int getHeaderCount();

    String getHeaderName(int headerIndex);
}
