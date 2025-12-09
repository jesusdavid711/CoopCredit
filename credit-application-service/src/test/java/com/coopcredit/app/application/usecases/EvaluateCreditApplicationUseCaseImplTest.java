package com.coopcredit.app.application.usecases;

import com.coopcredit.app.domain.exception.ApplicationNotFoundException;
import com.coopcredit.app.domain.model.Affiliate;
import com.coopcredit.app.domain.model.CreditApplication;
import com.coopcredit.app.domain.model.RiskEvaluation;
import com.coopcredit.app.domain.model.enums.AffiliateStatus;
import com.coopcredit.app.domain.model.enums.ApplicationStatus;
import com.coopcredit.app.domain.model.enums.RiskLevel;
import com.coopcredit.app.domain.port.out.AffiliateRepositoryPort;
import com.coopcredit.app.domain.port.out.CreditApplicationRepositoryPort;
import com.coopcredit.app.domain.port.out.RiskCentralPort;
import com.coopcredit.app.domain.port.out.RiskEvaluationRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EvaluateCreditApplicationUseCaseImplTest {

    @Mock
    private CreditApplicationRepositoryPort applicationRepository;

    @Mock
    private RiskCentralPort riskCentralPort;

    @Mock
    private RiskEvaluationRepositoryPort riskEvaluationRepository;

    @Mock
    private AffiliateRepositoryPort affiliateRepository;

    @InjectMocks
    private EvaluateCreditApplicationUseCaseImpl evaluateUseCase;

    private CreditApplication application;
    private Affiliate affiliate;

    @BeforeEach
    void setUp() {
        affiliate = createAffiliate();
        application = createApplication();
    }

    @Test
    void execute_WithHighScore_AutoApproves() {
        RiskEvaluation highScoreEvaluation = createRiskEvaluation(750, RiskLevel.BAJO);

        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
        when(affiliateRepository.findById(1L)).thenReturn(Optional.of(affiliate));
        when(riskCentralPort.evaluateRisk(anyString(), any(), anyInt())).thenReturn(highScoreEvaluation);
        when(riskEvaluationRepository.save(any())).thenReturn(highScoreEvaluation);
        when(applicationRepository.save(any())).thenReturn(application);

        CreditApplication result = evaluateUseCase.execute(1L);

        assertEquals(ApplicationStatus.APPROVED, result.getStatus());
        assertNull(result.getRejectionReason());
        verify(riskCentralPort).evaluateRisk(affiliate.getDocumentNumber(),
                application.getRequestedAmount(), application.getTermMonths());
    }

    @Test
    void execute_WithLowScore_AutoRejects() {
        RiskEvaluation lowScoreEvaluation = createRiskEvaluation(250, RiskLevel.ALTO);

        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
        when(affiliateRepository.findById(1L)).thenReturn(Optional.of(affiliate));
        when(riskCentralPort.evaluateRisk(anyString(), any(), anyInt())).thenReturn(lowScoreEvaluation);
        when(riskEvaluationRepository.save(any())).thenReturn(lowScoreEvaluation);
        when(applicationRepository.save(any())).thenReturn(application);

        CreditApplication result = evaluateUseCase.execute(1L);

        assertEquals(ApplicationStatus.REJECTED, result.getStatus());
        assertNotNull(result.getRejectionReason());
        assertTrue(result.getRejectionReason().contains("below minimum threshold"));
    }

    @Test
    void execute_WithHighRiskAndHighAmount_Rejects() {
        application.setRequestedAmount(new BigDecimal("6000000"));
        RiskEvaluation highRiskEvaluation = createRiskEvaluation(500, RiskLevel.ALTO);

        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
        when(affiliateRepository.findById(1L)).thenReturn(Optional.of(affiliate));
        when(riskCentralPort.evaluateRisk(anyString(), any(), anyInt())).thenReturn(highRiskEvaluation);
        when(riskEvaluationRepository.save(any())).thenReturn(highRiskEvaluation);
        when(applicationRepository.save(any())).thenReturn(application);

        CreditApplication result = evaluateUseCase.execute(1L);

        assertEquals(ApplicationStatus.REJECTED, result.getStatus());
        assertTrue(result.getRejectionReason().contains("exceeds limit for high-risk"));
    }

    @Test
    void execute_WithNonExistentApplication_ThrowsException() {
        when(applicationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ApplicationNotFoundException.class, () -> {
            evaluateUseCase.execute(999L);
        });

        verify(riskCentralPort, never()).evaluateRisk(anyString(), any(), anyInt());
    }

    private Affiliate createAffiliate() {
        Affiliate aff = new Affiliate();
        aff.setId(1L);
        aff.setDocumentNumber("12345678");
        aff.setName("Test Affiliate");
        aff.setSalary(new BigDecimal("3000000"));
        aff.setAffiliationDate(LocalDate.now().minusYears(1));
        aff.setStatus(AffiliateStatus.ACTIVE);
        return aff;
    }

    private CreditApplication createApplication() {
        CreditApplication app = new CreditApplication();
        app.setId(1L);
        app.setAffiliateId(1L);
        app.setRequestedAmount(new BigDecimal("5000000"));
        app.setTermMonths(24);
        app.setProposedRate(new BigDecimal("12.5"));
        app.setApplicationDate(LocalDateTime.now());
        app.setStatus(ApplicationStatus.PENDING);
        return app;
    }

    private RiskEvaluation createRiskEvaluation(Integer score, RiskLevel riskLevel) {
        RiskEvaluation evaluation = new RiskEvaluation();
        evaluation.setScore(score);
        evaluation.setRiskLevel(riskLevel);
        evaluation.setDetail("Test evaluation");
        evaluation.setEvaluationDate(LocalDateTime.now());
        return evaluation;
    }
}
