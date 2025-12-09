package com.coopcredit.app.domain.exception;

public class DuplicateDocumentException extends RuntimeException {

    public DuplicateDocumentException(String documentNumber) {
        super("Affiliate with document number " + documentNumber + " already exists");
    }
}
