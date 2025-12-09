package com.coopcredit.app.domain.model;

import com.coopcredit.app.domain.model.enums.AffiliateStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class Affiliate {

    private Long id;
    private String documentNumber;
    private String name;
    private BigDecimal salary;
    private LocalDate affiliationDate;
    private AffiliateStatus status;

    public Affiliate() {
    }

    public boolean isActive() {
        return AffiliateStatus.ACTIVE.equals(this.status);
    }

    public boolean hasMinimumSeniority(int minimumMonths) {
        if (affiliationDate == null) {
            return false;
        }
        LocalDate minimumDate = LocalDate.now().minusMonths(minimumMonths);
        return affiliationDate.isBefore(minimumDate) || affiliationDate.isEqual(minimumDate);
    }

    public BigDecimal getMaxCreditAmount(int multiplier) {
        return salary.multiply(BigDecimal.valueOf(multiplier));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public LocalDate getAffiliationDate() {
        return affiliationDate;
    }

    public void setAffiliationDate(LocalDate affiliationDate) {
        this.affiliationDate = affiliationDate;
    }

    public AffiliateStatus getStatus() {
        return status;
    }

    public void setStatus(AffiliateStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Affiliate affiliate = (Affiliate) o;
        return Objects.equals(id, affiliate.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
