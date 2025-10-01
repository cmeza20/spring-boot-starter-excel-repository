package com.cmeza.spring.excel.repository.support.exceptions;

public class WorkbookReadException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public WorkbookReadException(String message) {
        super(message);
    }

    public WorkbookReadException(String message, Throwable cause) {
        super(message, cause);
    }

    public WorkbookReadException(Throwable cause) {
        super(cause);
    }
}
