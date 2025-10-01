package com.cmeza.spring.excel.repository.configurations.beans;

import com.cmeza.spring.excel.repository.models.Customer;
import com.cmeza.spring.excel.repository.support.extensions.ItemErrorExtension;
import com.cmeza.spring.excel.repository.support.mappers.ToModelMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomerToModelMapper implements ToModelMapper<Customer> {
    @Override
    public void bindError(Customer entity, ItemErrorExtension<Customer> itemErrorExtension) {
        entity.setError(itemErrorExtension.getGroupedErrors());
    }
}
