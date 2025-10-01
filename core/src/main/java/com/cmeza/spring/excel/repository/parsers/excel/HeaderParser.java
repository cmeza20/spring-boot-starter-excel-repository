package com.cmeza.spring.excel.repository.parsers.excel;

import com.cmeza.spring.excel.repository.support.annotations.support.Header;
import com.cmeza.spring.excel.repository.support.configurations.excel.HeaderConfiguration;
import com.cmeza.spring.excel.repository.support.parsers.excel.IHeaderParser;
import org.apache.commons.lang3.StringUtils;

public class HeaderParser implements IHeaderParser<HeaderConfiguration, Header> {

    @Override
    public void parse(Header annotation, HeaderConfiguration headerConfiguration) {
        HeaderConfiguration headerConfigurationBean = new HeaderConfiguration();

        if (!annotation.header()) {
            headerConfigurationBean.setHeader(false);
        }
        if (annotation.freeze()) {
            headerConfigurationBean.setFreeze(true);
        }
        if (StringUtils.isNotEmpty(annotation.styleName())) {
            headerConfigurationBean.setStyleAlias(annotation.styleName());
        }
        if (annotation.headerHeightMultiplier() != -1 && annotation.headerHeightMultiplier() > 0) {
            headerConfigurationBean.setHeightMultiplier(annotation.headerHeightMultiplier());
        }

        merge(headerConfigurationBean, headerConfiguration);
    }

    @Override
    public void merge(HeaderConfiguration origin, HeaderConfiguration target) {
        if(!origin.isHeader()) {
            target.setHeader(false);
        }
        if(origin.isFreeze()) {
            target.setFreeze(true);
        }
        if(StringUtils.isNotEmpty(origin.getStyleAlias())) {
            target.setStyleAlias(origin.getStyleAlias());
        }
        if(origin.getHeightMultiplier() != 1 && origin.getHeightMultiplier() > 1) {
            target.setHeightMultiplier(origin.getHeightMultiplier());
        }
    }
}
