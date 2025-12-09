package com.coopcredit.app.domain.exception;

public class InactiveAffiliateException extends RuntimeException {

    public InactiveAffiliateException(String documentNumber) {
        super("Affiliate with document " + documentNumber + " is inactive and cannot request credit");
    }
}
