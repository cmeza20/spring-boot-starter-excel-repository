package com.cmeza.spring.excel.repository.configurations.beans;

import com.cmeza.spring.excel.repository.models.Customer;
import com.cmeza.spring.excel.repository.models.SimpleCustomer;
import com.cmeza.spring.excel.repository.support.maps.MapModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomerMapModel implements MapModel<Customer, SimpleCustomer> {

    @Override
    public SimpleCustomer map(int position, Customer customer) {
        return new SimpleCustomer()
                .setId(customer.getCustomerId())
                .setName(customer.getCustomerName())
                .setCode(customer.getCustomerCode())
                .setAddress(customer.getAddress())
                .setApprobation(customer.getApprobation())
                .setEmployeeName(customer.getEmployee().getName());
    }
}
