package com.coopcredit.app.domain.model;

import com.coopcredit.app.domain.model.enums.ApplicationStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class CreditApplication {

    private Long id;
    private Long affiliateId;
    private BigDecimal requestedAmount;
    private Integer termMonths;
    private BigDecimal proposedRate;
    private LocalDateTime applicationDate;
    private ApplicationStatus status;
    private RiskEvaluation riskEvaluation;
    private String rejectionReason;

    public CreditApplication() {
    }

    public BigDecimal calculateMonthlyPayment() {
        if (requestedAmount == null || termMonths == null || proposedRate == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal monthlyRate = proposedRate.divide(BigDecimal.valueOf(100 * 12), 6, BigDecimal.ROUND_HALF_UP);
        BigDecimal onePlusRate = BigDecimal.ONE.add(monthlyRate);
        BigDecimal power = onePlusRate.pow(termMonths);

        BigDecimal numerator = requestedAmount.multiply(monthlyRate).multiply(power);
        BigDecimal denominator = power.subtract(BigDecimal.ONE);

        return numerator.divide(denominator, 2, BigDecimal.ROUND_HALF_UP);
    }

    public void approve() {
        this.status = ApplicationStatus.APPROVED;
        this.rejectionReason = null;
    }

    public void reject(String reason) {
        this.status = ApplicationStatus.REJECTED;
        this.rejectionReason = reason;
    }

    public boolean isPending() {
        return ApplicationStatus.PENDING.equals(this.status);
    }

    public boolean isApproved() {
        return ApplicationStatus.APPROVED.equals(this.status);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAffiliateId() {
        return affiliateId;
    }

    public void setAffiliateId(Long affiliateId) {
        this.affiliateId = affiliateId;
    }

    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }

    public void setRequestedAmount(BigDecimal requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public Integer getTermMonths() {
        return termMonths;
    }

    public void setTermMonths(Integer termMonths) {
        this.termMonths = termMonths;
    }

    public BigDecimal getProposedRate() {
        return proposedRate;
    }

    public void setProposedRate(BigDecimal proposedRate) {
        this.proposedRate = proposedRate;
    }

    public LocalDateTime getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(LocalDateTime applicationDate) {
        this.applicationDate = applicationDate;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public RiskEvaluation getRiskEvaluation() {
        return riskEvaluation;
    }

    public void setRiskEvaluation(RiskEvaluation riskEvaluation) {
        this.riskEvaluation = riskEvaluation;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CreditApplication that = (CreditApplication) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
