package com.cmeza.spring.excel.repository.support.exceptions;

public class WorkbookNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public WorkbookNotFoundException(String message) {
        super(message);
    }

    public WorkbookNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public WorkbookNotFoundException(Throwable cause) {
        super(cause);
    }
}
