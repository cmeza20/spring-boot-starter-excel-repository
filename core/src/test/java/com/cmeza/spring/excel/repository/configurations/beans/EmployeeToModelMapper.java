package com.cmeza.spring.excel.repository.configurations.beans;

import com.cmeza.spring.excel.repository.models.Employee;
import com.cmeza.spring.excel.repository.support.extensions.ItemErrorExtension;
import com.cmeza.spring.excel.repository.support.mappers.ToModelMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmployeeToModelMapper implements ToModelMapper<Employee> {
    @Override
    public void bindError(Employee entity, ItemErrorExtension<Employee> itemErrorExtension) {
        entity.setError(itemErrorExtension.getGroupedErrors());
    }
}
