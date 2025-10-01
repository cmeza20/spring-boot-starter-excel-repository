package com.cmeza.spring.excel.repository.parsers.model;

import com.cmeza.spring.excel.repository.dsl.models.ErrorDsl;
import com.cmeza.spring.excel.repository.support.annotations.support.Error;
import com.cmeza.spring.excel.repository.support.configurations.model.ErrorConfiguration;
import com.cmeza.spring.excel.repository.support.parsers.model.IErrorParser;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.util.Objects;

public class ErrorParser implements IErrorParser<ErrorConfiguration, Error, ErrorDsl> {

    @Override
    public void parse(Error annotation, ErrorConfiguration errorConfiguration) {
        if (StringUtils.isNotEmpty(annotation.fileName())) {
            errorConfiguration.setFileName(annotation.fileName());
        }
        if (StringUtils.isNotEmpty(annotation.folder())) {
            errorConfiguration.setFolder(Path.of(annotation.folder()));
        }
        if (annotation.versioned()) {
            errorConfiguration.setVersioned(true);
        }
    }

    @Override
    public void merge(ErrorConfiguration origin, ErrorConfiguration target) {
        if (StringUtils.isNotEmpty(origin.getFileName())) {
            target.setFileName(target.getFileName());
        }
        if (Objects.nonNull(origin.getFolder())) {
            target.setFolder(origin.getFolder());
        }
        target.setVersioned(origin.isVersioned());
    }

    @Override
    public void parseDsl(Error error, ErrorDsl dsl) {
        if (StringUtils.isNotEmpty(error.fileName())) {
            dsl.setFileName(error.fileName());
        }
        if (StringUtils.isNotEmpty(error.folder())) {
            dsl.setFolder(error.folder());
        }
        if (error.versioned()) {
            dsl.setVersioned(true);
        }
    }
}
