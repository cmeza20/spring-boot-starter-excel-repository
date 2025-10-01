package com.cmeza.spring.excel.repository.support.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

@UtilityClass
public class SupportUtils {
    public static final String EXTENSION = "xlsx";

    public String generateDefaultFileName(String prefix) {
        return makeFileNameWithPrefix(prefix, UUID.randomUUID().toString().replace("-", "") + "." + EXTENSION, false, true);
    }

    public String makeFileNameWithPrefix(String prefix, String filename, boolean versioned, boolean defaultName) {
        if (!defaultName && versioned) {
            String name = FilenameUtils.getBaseName(filename);
            String extension = FilenameUtils.getExtension(filename);
            filename = name + "-" + System.currentTimeMillis() + "." + extension;
        }

        return (StringUtils.isNotEmpty(prefix) ? prefix : "") + filename;
    }
}
