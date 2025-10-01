package com.cmeza.spring.excel.repository.models;

import com.cmeza.spring.excel.repository.support.annotations.model.Column;
import constraints.Equals;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDate;

@Data
public class Employee {
    @Min(value = 200, message = "Must be greater than 200")
    @Max(value = 1000, message = "Must be less than 1000")
    @Column("identification")
    private Long id;

    @Column(ignored = true)
    private String name;

    @Equals("lastName")
    @Column(value = "last_name", styleName = "CUSTOM_COLUMN_STYLE")
    private String lastName;

    @Column(value = "birth_date")
    private LocalDate birthDate;

    @Column(mapping = "title.name", header = "title_name")
    private Title title;

    @Column
    private String error;
}
