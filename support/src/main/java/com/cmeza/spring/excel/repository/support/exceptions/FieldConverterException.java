package com.cmeza.spring.excel.repository.support.exceptions;

public class FieldConverterException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public FieldConverterException(String message) {
        super(message);
    }

    public FieldConverterException(String message, Throwable cause) {
        super(message, cause);
    }

    public FieldConverterException(Throwable cause) {
        super(cause);
    }
}
