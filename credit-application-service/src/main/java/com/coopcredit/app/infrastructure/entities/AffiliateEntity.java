package com.coopcredit.app.infrastructure.entities;

import com.coopcredit.app.domain.model.enums.AffiliateStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "affiliates")
public class AffiliateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "document_number", unique = true, nullable = false, length = 20)
    private String documentNumber;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal salary;

    @Column(name = "affiliation_date", nullable = false)
    private LocalDate affiliationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AffiliateStatus status;

    @OneToMany(mappedBy = "affiliate", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CreditApplicationEntity> applications;

    public AffiliateEntity() {
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

    public List<CreditApplicationEntity> getApplications() {
        return applications;
    }

    public void setApplications(List<CreditApplicationEntity> applications) {
        this.applications = applications;
    }
}
