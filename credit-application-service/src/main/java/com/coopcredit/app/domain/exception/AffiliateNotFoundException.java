package com.coopcredit.app.domain.exception;

public class AffiliateNotFoundException extends RuntimeException {

    public AffiliateNotFoundException(Long id) {
        super("Affiliate not found with id: " + id);
    }

    public AffiliateNotFoundException(String documentNumber) {
        super("Affiliate not found with document number: " + documentNumber);
    }
}
