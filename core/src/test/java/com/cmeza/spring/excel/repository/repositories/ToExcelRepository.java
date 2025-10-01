package com.cmeza.spring.excel.repository.repositories;

import com.cmeza.spring.excel.repository.support.annotations.ExcelRepository;
import com.cmeza.spring.excel.repository.support.annotations.methods.ToExcel;
import com.cmeza.spring.excel.repository.support.annotations.model.Sheet;
import com.cmeza.spring.excel.repository.support.annotations.model.Style;
import com.cmeza.spring.excel.repository.support.annotations.support.Mapping;
import com.cmeza.spring.excel.repository.configurations.beans.LogToExcelInterceptor;
import com.cmeza.spring.excel.repository.configurations.beans.SimpleToExcelMapper;
import com.cmeza.spring.excel.repository.models.Customer;
import com.cmeza.spring.excel.repository.models.Employee;
import com.cmeza.spring.excel.repository.models.Title;

import java.nio.file.Path;
import java.util.List;

@ExcelRepository
public interface ToExcelRepository {

    @ToExcel
    @Style(name = "customColumnStyle")
    Path employeeReportWithAnnotationBeansAndStyles(List<Employee> employeeList);

    @Sheet("customSheetConfiguration")
    @ToExcel(versioned = true, prefix = "TIT-")
    Path titleReportWithAnnotationAndStyles(List<Title> employeeList);

    @ToExcel
    @Sheet(name = "CUSTOMERS", mappings = {
            @Mapping(value = "customerId", headerName = "ID"),
            @Mapping(value = "customerName", headerName = "NAME"),
    })
    Path customerReportWithMappings(List<Customer> customerList);

    @ToExcel
    @Sheet("customerSheetConfiguration")
    @Sheet("customSheetConfiguration")
    Path customersAndEmployeesReport(List<Customer> customerList, List<Employee> employeeList);

    @ToExcel(interceptor = LogToExcelInterceptor.class)
    Path customerReportWithInterceptor(List<Customer> customerList);

    @ToExcel(mapper = SimpleToExcelMapper.class)
    Path customerReportWithMapper(List<Customer> customerList);
}
