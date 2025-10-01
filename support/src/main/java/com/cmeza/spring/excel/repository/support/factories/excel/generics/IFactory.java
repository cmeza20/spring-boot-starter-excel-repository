package com.cmeza.spring.excel.repository.support.factories.excel.generics;


import com.cmeza.spring.excel.repository.support.interceptors.ToExcelInterceptor;
import com.cmeza.spring.excel.repository.support.mappers.ToExcelMapper;

public interface IFactory<T, E> {
    T build(E entity, ToExcelInterceptor interceptor, ToExcelMapper toExcelMapper);
}
