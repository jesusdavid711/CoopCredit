package com.coopcredit.app.domain.exception;

public class ApplicationNotFoundException extends RuntimeException {

    public ApplicationNotFoundException(Long id) {
        super("Credit application not found with id: " + id);
    }
}
