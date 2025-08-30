package com.reliaquest.api.exception;

public class EmployeeNotFoundException extends RuntimeException {
    public EmployeeNotFoundException(String details) {
        super(details);
    }
}
