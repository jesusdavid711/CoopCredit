package com.coopcredit.app.domain.model;

import com.coopcredit.app.domain.model.enums.ApplicationStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CreditApplicationTest {

    @Test
    void testCalculateMonthlyPayment_WithValidData() {
        CreditApplication application = createApplication(
                new BigDecimal("10000000"),
                12,
                new BigDecimal("12.5"));

        BigDecimal monthlyPayment = application.calculateMonthlyPayment();

        assertNotNull(monthlyPayment);
        assertTrue(monthlyPayment.compareTo(BigDecimal.ZERO) > 0);
        // Monthly payment should be around 880k-900k depending on formula
        assertTrue(monthlyPayment.compareTo(new BigDecimal("800000")) > 0);
        assertTrue(monthlyPayment.compareTo(new BigDecimal("920000")) < 0);
    }

    @Test
    void testCalculateMonthlyPayment_WithNullValues_ReturnsZero() {
        CreditApplication application = new CreditApplication();

        BigDecimal monthlyPayment = application.calculateMonthlyPayment();

        assertEquals(BigDecimal.ZERO, monthlyPayment);
    }

    @Test
    void testApprove_ChangesStatusToApproved() {
        CreditApplication application = createApplication(
                new BigDecimal("5000000"),
                24,
                new BigDecimal("10.0"));
        application.setStatus(ApplicationStatus.PENDING);

        application.approve();

        assertEquals(ApplicationStatus.APPROVED, application.getStatus());
        assertNull(application.getRejectionReason());
    }

    @Test
    void testReject_ChangesStatusAndSetsReason() {
        CreditApplication application = createApplication(
                new BigDecimal("5000000"),
                24,
                new BigDecimal("10.0"));
        application.setStatus(ApplicationStatus.PENDING);
        String reason = "Low credit score";

        application.reject(reason);

        assertEquals(ApplicationStatus.REJECTED, application.getStatus());
        assertEquals(reason, application.getRejectionReason());
    }

    @Test
    void testIsPending_WhenStatusPending_ReturnsTrue() {
        CreditApplication application = createApplication(
                new BigDecimal("5000000"),
                24,
                new BigDecimal("10.0"));
        application.setStatus(ApplicationStatus.PENDING);

        assertTrue(application.isPending());
    }

    @Test
    void testIsPending_WhenStatusApproved_ReturnsFalse() {
        CreditApplication application = createApplication(
                new BigDecimal("5000000"),
                24,
                new BigDecimal("10.0"));
        application.setStatus(ApplicationStatus.APPROVED);

        assertFalse(application.isPending());
    }

    @Test
    void testIsApproved_WhenStatusApproved_ReturnsTrue() {
        CreditApplication application = createApplication(
                new BigDecimal("5000000"),
                24,
                new BigDecimal("10.0"));
        application.setStatus(ApplicationStatus.APPROVED);

        assertTrue(application.isApproved());
    }

    private CreditApplication createApplication(BigDecimal amount, Integer termMonths, BigDecimal rate) {
        CreditApplication application = new CreditApplication();
        application.setId(1L);
        application.setAffiliateId(1L);
        application.setRequestedAmount(amount);
        application.setTermMonths(termMonths);
        application.setProposedRate(rate);
        application.setApplicationDate(LocalDateTime.now());
        application.setStatus(ApplicationStatus.PENDING);
        return application;
    }
}
