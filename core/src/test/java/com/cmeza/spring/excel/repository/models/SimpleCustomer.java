package com.cmeza.spring.excel.repository.models;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SimpleCustomer {
    private Long id;
    private String name;
    private String code;
    private String address;
    private LocalDateTime approbation;
    private String employeeName;
}
