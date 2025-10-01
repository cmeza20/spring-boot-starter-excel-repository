package com.cmeza.spring.excel.repository.repositories;

import com.cmeza.spring.excel.repository.configurations.beans.CustomerToModelMapper;
import com.cmeza.spring.excel.repository.models.Customer;
import com.cmeza.spring.excel.repository.models.SimpleCustomer;
import com.cmeza.spring.excel.repository.support.annotations.ExcelRepository;
import com.cmeza.spring.excel.repository.support.annotations.methods.ToModel;
import com.cmeza.spring.excel.repository.support.annotations.support.Error;
import com.cmeza.spring.excel.repository.support.annotations.support.Mapping;
import com.cmeza.spring.excel.repository.configurations.beans.CustomerMapModel;
import com.cmeza.spring.excel.repository.configurations.beans.EmployeeToModelMapper;
import com.cmeza.spring.excel.repository.models.Employee;
import com.cmeza.spring.excel.repository.support.results.*;

import java.io.File;
import java.util.List;

@ExcelRepository(loggable = true)
public interface ToModelRepository {

    @ToModel(mappings = @Mapping(value = "title.id", headerName = "title_id"))
    List<Employee> readEmployees(File file);

    @ToModel(mappings = @Mapping(value = "title.id", headerName = "title_id"), mapper = EmployeeToModelMapper.class)
    Validated<Employee> validatedEmployeesWithModelMapper(File file);

    @ToModel(mappings = @Mapping(value = "title.id", headerName = "title_id"), mapper = EmployeeToModelMapper.class)
    ValidatedError<Employee> validatedErrorEmployeesWithModelMapper(File file);

    @ToModel(mappings = {
            @Mapping(value = "customerId", headerName = "ID"),
            @Mapping(value = "customerName", headerName = "NAME"),
            @Mapping(value = "customerCode", headerName = "CODE"),
            @Mapping(value = "address", headerName = "ADDRESS"),
            @Mapping(value = "employee.id", headerName = "EMP ID"),
            @Mapping(value = "employee.name", headerName = "EMP NAME"),
            @Mapping(value = "size", headerName = "SIZE"),
            @Mapping(value = "createdAt", headerName = "CREATED AT"),
            @Mapping(value = "modifiedAt", headerName = "MODIFIED AT"),
            @Mapping(value = "approbation", headerName = "APPROBATION"),
            @Mapping(value = "officeHours", headerName = "OFFICE HOURS"),
    }, mapper = CustomerToModelMapper.class, map = CustomerMapModel.class)
    MapValidated<Customer, SimpleCustomer> validatedWithMapTransform(File file);


    @ToModel(mappings = @Mapping(value = "title.id", headerName = "title_id"),
            mapper = EmployeeToModelMapper.class,
            error = @Error(fileName = "validatedWithExcelError.xlsx", versioned = false))
    ExcelValidated<Employee> validatedWithExcelError(File file);


    @ToModel
    ExcelValidated<Employee> validatedWithExcelErrorDsl(File file);
}
