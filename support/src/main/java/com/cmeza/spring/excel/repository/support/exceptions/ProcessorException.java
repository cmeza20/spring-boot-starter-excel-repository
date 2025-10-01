package com.cmeza.spring.excel.repository.support.exceptions;

public class ProcessorException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ProcessorException(String message) {
        super(message);
    }

    public ProcessorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProcessorException(Throwable cause) {
        super(cause);
    }
}
