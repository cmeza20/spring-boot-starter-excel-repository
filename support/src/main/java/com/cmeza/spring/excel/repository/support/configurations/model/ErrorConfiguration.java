package com.cmeza.spring.excel.repository.support.configurations.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.file.Path;

@Data
@NoArgsConstructor
public class ErrorConfiguration {
    private String fileName;
    private Path folder;
    private boolean versioned;
}
