package com.coopcredit.app.infrastructure.entities;

import com.coopcredit.app.domain.model.enums.RiskLevel;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "risk_evaluations")
public class RiskEvaluationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credit_application_id", unique = true)
    private CreditApplicationEntity creditApplication;

    @Column(name = "document_number", nullable = false, length = 20)
    private String documentNumber;

    @Column(nullable = false)
    private Integer score;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false, length = 20)
    private RiskLevel riskLevel;

    @Column(columnDefinition = "TEXT")
    private String detail;

    @Column(name = "evaluation_date", nullable = false)
    private LocalDateTime evaluationDate;

    public RiskEvaluationEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CreditApplicationEntity getCreditApplication() {
        return creditApplication;
    }

    public void setCreditApplication(CreditApplicationEntity creditApplication) {
        this.creditApplication = creditApplication;
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
}
