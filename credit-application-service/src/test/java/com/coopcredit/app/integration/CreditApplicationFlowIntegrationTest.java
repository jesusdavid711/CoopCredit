package com.coopcredit.app.integration;

import com.coopcredit.app.domain.model.enums.ApplicationStatus;
import com.coopcredit.app.infrastructure.entities.AffiliateEntity;
import com.coopcredit.app.infrastructure.entities.CreditApplicationEntity;
import com.coopcredit.app.infrastructure.entities.RiskEvaluationEntity;
import com.coopcredit.app.infrastructure.repositories.AffiliateJpaRepository;
import com.coopcredit.app.infrastructure.repositories.CreditApplicationJpaRepository;
import com.coopcredit.app.infrastructure.repositories.RiskEvaluationJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.coopcredit.app.domain.model.enums.AffiliateStatus.ACTIVE;
import static com.coopcredit.app.domain.model.enums.RiskLevel.BAJO;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CreditApplicationFlowIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> "false");
    }

    @Autowired
    private AffiliateJpaRepository affiliateRepository;

    @Autowired
    private CreditApplicationJpaRepository applicationRepository;

    @Autowired
    private RiskEvaluationJpaRepository riskEvaluationRepository;

    private AffiliateEntity affiliate;

    @BeforeEach
    void setUp() {
        affiliate = createAndSaveAffiliate();
    }

    @Test
    void testCompleteFlow_CreateAffiliate_CreateApplication_AddRiskEvaluation() {
        // 1. Verify affiliate was created
        assertNotNull(affiliate.getId());
        assertEquals("12345678", affiliate.getDocumentNumber());

        // 2. Create credit application
        CreditApplicationEntity application = createCreditApplication(affiliate);
        CreditApplicationEntity savedApp = applicationRepository.save(application);

        assertNotNull(savedApp.getId());
        assertEquals(affiliate.getId(), savedApp.getAffiliate().getId());
        assertEquals(ApplicationStatus.PENDING, savedApp.getStatus());

        // 3. Create risk evaluation
        RiskEvaluationEntity risk = createRiskEvaluation();
        risk.setCreditApplication(savedApp);
        RiskEvaluationEntity savedRisk = riskEvaluationRepository.save(risk);

        assertNotNull(savedRisk.getId());
        assertEquals(750, savedRisk.getScore());
        assertEquals(BAJO, savedRisk.getRiskLevel());

        // 4. Update application with risk
        savedApp.setRiskEvaluation(savedRisk);
        savedApp.setStatus(ApplicationStatus.APPROVED);
        CreditApplicationEntity updatedApp = applicationRepository.save(savedApp);

        assertEquals(ApplicationStatus.APPROVED, updatedApp.getStatus());
        assertNotNull(updatedApp.getRiskEvaluation());
    }

    @Test
    void testFindApplicationsByAffiliate() {
        // Create multiple applications for same affiliate
        CreditApplicationEntity app1 = createCreditApplication(affiliate);
        app1.setRequestedAmount(new BigDecimal("5000000"));
        applicationRepository.save(app1);

        CreditApplicationEntity app2 = createCreditApplication(affiliate);
        app2.setRequestedAmount(new BigDecimal("8000000"));
        applicationRepository.save(app2);

        // Find by affiliate
        List<CreditApplicationEntity> applications = applicationRepository.findByAffiliateId(affiliate.getId());

        assertEquals(2, applications.size());
        assertTrue(
                applications.stream().anyMatch(a -> a.getRequestedAmount().compareTo(new BigDecimal("5000000")) == 0));
        assertTrue(
                applications.stream().anyMatch(a -> a.getRequestedAmount().compareTo(new BigDecimal("8000000")) == 0));
    }

    @Test
    void testJPARelationships_OneToMany_Affiliate_Applications() {
        // Create applications
        CreditApplicationEntity app1 = createCreditApplication(affiliate);
        CreditApplicationEntity app2 = createCreditApplication(affiliate);

        applicationRepository.save(app1);
        applicationRepository.save(app2);

        // Fetch affiliate with applications
        AffiliateEntity foundAffiliate = affiliateRepository.findById(affiliate.getId()).orElseThrow();

        assertNotNull(foundAffiliate.getApplications());
        assertEquals(2, foundAffiliate.getApplications().size());
    }

    @Test
    void testJPARelationships_OneToOne_Application_Risk() {
        CreditApplicationEntity application = createCreditApplication(affiliate);
        CreditApplicationEntity savedApp = applicationRepository.save(application);

        RiskEvaluationEntity risk = createRiskEvaluation();
        risk.setCreditApplication(savedApp);
        RiskEvaluationEntity savedRisk = riskEvaluationRepository.save(risk);

        savedApp.setRiskEvaluation(savedRisk);
        applicationRepository.save(savedApp);

        // Fetch and verify relationship
        CreditApplicationEntity found = applicationRepository.findById(savedApp.getId()).orElseThrow();

        assertNotNull(found.getRiskEvaluation());
        assertEquals(savedRisk.getId(), found.getRiskEvaluation().getId());
        assertEquals(found.getId(), found.getRiskEvaluation().getCreditApplication().getId());
    }

    @Test
    void testTransactionRollback_OnConstraintViolation() {
        // Try to create application with null required field
        CreditApplicationEntity invalidApp = new CreditApplicationEntity();
        invalidApp.setAffiliate(affiliate);
        // Missing required fields

        assertThrows(Exception.class, () -> {
            applicationRepository.saveAndFlush(invalidApp);
        });
    }

    private AffiliateEntity createAndSaveAffiliate() {
        AffiliateEntity aff = new AffiliateEntity();
        aff.setDocumentNumber("12345678");
        aff.setName("Test Affiliate");
        aff.setSalary(new BigDecimal("3000000"));
        aff.setAffiliationDate(LocalDate.now().minusYears(1));
        aff.setStatus(ACTIVE);
        return affiliateRepository.save(aff);
    }

    private CreditApplicationEntity createCreditApplication(AffiliateEntity affiliate) {
        CreditApplicationEntity app = new CreditApplicationEntity();
        app.setAffiliate(affiliate);
        app.setRequestedAmount(new BigDecimal("10000000"));
        app.setTermMonths(24);
        app.setProposedRate(new BigDecimal("12.5"));
        app.setApplicationDate(LocalDateTime.now());
        app.setStatus(ApplicationStatus.PENDING);
        return app;
    }

    private RiskEvaluationEntity createRiskEvaluation() {
        RiskEvaluationEntity risk = new RiskEvaluationEntity();
        risk.setScore(750);
        risk.setRiskLevel(BAJO);
        risk.setDetail("Excellent credit history");
        risk.setEvaluationDate(LocalDateTime.now());
        return risk;
    }
}
