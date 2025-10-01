package com.cmeza.spring.excel.repository.support.exceptions;

public class ClassConverterException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ClassConverterException(String message) {
        super(message);
    }

    public ClassConverterException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClassConverterException(Throwable cause) {
        super(cause);
    }
}
