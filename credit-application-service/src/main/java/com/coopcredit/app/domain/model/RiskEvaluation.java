package com.coopcredit.app.domain.model;

import com.coopcredit.app.domain.model.enums.RiskLevel;

import java.time.LocalDateTime;
import java.util.Objects;

public class RiskEvaluation {

    private Long id;
    private String documentNumber;
    private Integer score;
    private RiskLevel riskLevel;
    private String detail;
    private LocalDateTime evaluationDate;
    private CreditApplication creditApplication;

    public RiskEvaluation() {
    }

    public boolean isLowRisk() {
        return RiskLevel.BAJO.equals(this.riskLevel);
    }

    public boolean isHighRisk() {
        return RiskLevel.ALTO.equals(this.riskLevel);
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

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public LocalDateTime getEvaluationDate() {
        return evaluationDate;
    }

    public void setEvaluationDate(LocalDateTime evaluationDate) {
        this.evaluationDate = evaluationDate;
    }

    public CreditApplication getCreditApplication() {
        return creditApplication;
    }

    public void setCreditApplication(CreditApplication creditApplication) {
        this.creditApplication = creditApplication;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        RiskEvaluation that = (RiskEvaluation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
