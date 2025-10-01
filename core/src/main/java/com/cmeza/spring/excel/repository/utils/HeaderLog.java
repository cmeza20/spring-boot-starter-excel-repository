package com.cmeza.spring.excel.repository.utils;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.function.Consumer;

@Data
public class HeaderLog {
    private Logger logger;
    private boolean loggable;
    private String className;
    private Consumer<Logger> printExtras;
    private String excelRepositoryTemplateBeanName;
    private String key;

    public boolean hasKey() {
        return !StringUtils.isEmpty(key);
    }

}
