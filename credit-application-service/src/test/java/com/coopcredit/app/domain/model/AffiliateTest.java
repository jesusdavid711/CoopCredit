package com.coopcredit.app.domain.model;

import com.coopcredit.app.domain.model.enums.AffiliateStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class AffiliateTest {

    @Test
    void testIsActive_WhenStatusActive_ReturnsTrue() {
        Affiliate affiliate = createAffiliate(AffiliateStatus.ACTIVE);

        assertTrue(affiliate.isActive());
    }

    @Test
    void testIsActive_WhenStatusInactive_ReturnsFalse() {
        Affiliate affiliate = createAffiliate(AffiliateStatus.INACTIVE);

        assertFalse(affiliate.isActive());
    }

    @Test
    void testHasMinimumSeniority_WithExactMinimum_ReturnsTrue() {
        Affiliate affiliate = createAffiliate(AffiliateStatus.ACTIVE);
        affiliate.setAffiliationDate(LocalDate.now().minusMonths(6));

        assertTrue(affiliate.hasMinimumSeniority(6));
    }

    @Test
    void testHasMinimumSeniority_WithMoreThanMinimum_ReturnsTrue() {
        Affiliate affiliate = createAffiliate(AffiliateStatus.ACTIVE);
        affiliate.setAffiliationDate(LocalDate.now().minusMonths(12));

        assertTrue(affiliate.hasMinimumSeniority(6));
    }

    @Test
    void testHasMinimumSeniority_WithLessThanMinimum_ReturnsFalse() {
        Affiliate affiliate = createAffiliate(AffiliateStatus.ACTIVE);
        affiliate.setAffiliationDate(LocalDate.now().minusMonths(3));

        assertFalse(affiliate.hasMinimumSeniority(6));
    }

    @Test
    void testGetMaxCreditAmount_WithMultiplier5() {
        Affiliate affiliate = createAffiliate(AffiliateStatus.ACTIVE);
        affiliate.setSalary(new BigDecimal("2000000"));

        BigDecimal maxCredit = affiliate.getMaxCreditAmount(5);

        assertEquals(new BigDecimal("10000000"), maxCredit);
    }

    @Test
    void testGetMaxCreditAmount_WithDifferentSalary() {
        Affiliate affiliate = createAffiliate(AffiliateStatus.ACTIVE);
        affiliate.setSalary(new BigDecimal("3500000"));

        BigDecimal maxCredit = affiliate.getMaxCreditAmount(5);

        assertEquals(new BigDecimal("17500000"), maxCredit);
    }

    private Affiliate createAffiliate(AffiliateStatus status) {
        Affiliate affiliate = new Affiliate();
        affiliate.setId(1L);
        affiliate.setDocumentNumber("12345678");
        affiliate.setName("Test Affiliate");
        affiliate.setSalary(new BigDecimal("2000000"));
        affiliate.setAffiliationDate(LocalDate.now().minusYears(1));
        affiliate.setStatus(status);
        return affiliate;
    }
}
