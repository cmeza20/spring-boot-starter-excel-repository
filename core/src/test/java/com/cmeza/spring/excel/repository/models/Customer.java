package com.cmeza.spring.excel.repository.models;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

@Data
public class Customer {
    private Long customerId;
    private String customerName;
    private String customerCode;
    private String address;
    private Double size;
    private Date createdAt;
    private LocalDate modifiedAt;
    private LocalDateTime approbation;
    private LocalTime officeHours;

    private Employee employee;

    private String error;
}
