package com.cmeza.spring.excel.repository.support.mappers;

import com.cmeza.spring.excel.repository.support.extensions.ConverterMapperExtension;
import com.cmeza.spring.excel.repository.support.extensions.FactoryMapperExtension;
import com.cmeza.spring.excel.repository.support.extensions.ValidateMapperExtension;
import com.cmeza.spring.ioc.handler.utils.IocUtil;
import org.springframework.beans.BeanUtils;

@SuppressWarnings("all")
public interface ToModelMapper<T> extends FactoryMapperExtension<T>, ConverterMapperExtension, ValidateMapperExtension<T> {

    default T toInstance() {
        return BeanUtils.instantiateClass(getClazz());
    }

    default Object toInstance(Class<?> clazz) {
        return BeanUtils.instantiateClass(clazz);
    }

    default Class<T> getClazz() {
        return (Class<T>) IocUtil.getGenericInterface(this);
    }

}
