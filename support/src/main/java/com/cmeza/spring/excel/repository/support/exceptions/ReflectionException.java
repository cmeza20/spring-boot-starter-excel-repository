package com.cmeza.spring.excel.repository.support.exceptions;

public class ReflectionException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ReflectionException(String message) {
        super(message);
    }

    public ReflectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReflectionException(Throwable cause) {
        super(cause);
    }
}
