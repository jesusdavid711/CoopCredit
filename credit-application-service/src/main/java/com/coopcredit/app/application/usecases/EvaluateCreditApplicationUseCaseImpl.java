package com.coopcredit.app.application.usecases;

import com.coopcredit.app.domain.exception.ApplicationNotFoundException;
import com.coopcredit.app.domain.exception.BusinessValidationException;
import com.coopcredit.app.domain.model.CreditApplication;
import com.coopcredit.app.domain.model.RiskEvaluation;
import com.coopcredit.app.domain.model.enums.RiskLevel;
import com.coopcredit.app.domain.port.in.EvaluateCreditApplicationUseCase;
import com.coopcredit.app.domain.port.out.AffiliateRepositoryPort;
import com.coopcredit.app.domain.port.out.CreditApplicationRepositoryPort;
import com.coopcredit.app.domain.port.out.RiskCentralPort;
import com.coopcredit.app.domain.port.out.RiskEvaluationRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class EvaluateCreditApplicationUseCaseImpl implements EvaluateCreditApplicationUseCase {

    private static final int AUTO_APPROVE_SCORE_THRESHOLD = 700;
    private static final int AUTO_REJECT_SCORE_THRESHOLD = 300;
    private static final BigDecimal HIGH_RISK_MAX_AMOUNT = new BigDecimal("5000000");
    private static final int HIGH_RISK_MAX_TERM = 12;

    private final CreditApplicationRepositoryPort applicationRepository;
    private final RiskCentralPort riskCentralPort;
    private final RiskEvaluationRepositoryPort riskEvaluationRepository;
    private final AffiliateRepositoryPort affiliateRepository;

    public EvaluateCreditApplicationUseCaseImpl(
            CreditApplicationRepositoryPort applicationRepository,
            RiskCentralPort riskCentralPort,
            RiskEvaluationRepositoryPort riskEvaluationRepository,
            AffiliateRepositoryPort affiliateRepository) {
        this.applicationRepository = applicationRepository;
        this.riskCentralPort = riskCentralPort;
        this.riskEvaluationRepository = riskEvaluationRepository;
        this.affiliateRepository = affiliateRepository;
    }

    @Override
    public CreditApplication execute(Long applicationId) {
        CreditApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException(applicationId));

        if (!application.isPending()) {
            throw new BusinessValidationException("Application has already been evaluated");
        }

        var affiliate = affiliateRepository.findById(application.getAffiliateId())
                .orElseThrow(() -> new BusinessValidationException("Affiliate not found"));

        // Calculate debt-to-income ratio
        BigDecimal monthlyPayment = application.calculateMonthlyPayment();
        BigDecimal monthlyIncome = affiliate.getSalary();
        BigDecimal debtToIncomeRatio = monthlyIncome.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO // Avoid division by zero
                : monthlyPayment.divide(monthlyIncome, 4, RoundingMode.HALF_UP);

        if (debtToIncomeRatio.compareTo(new BigDecimal("0.40")) > 0) {
            application.reject("Debt-to-income ratio exceeds 40%");
            return applicationRepository.save(application);
        }

        RiskEvaluation riskEvaluation = riskCentralPort.evaluateRisk(
                affiliate.getDocumentNumber(),
                application.getRequestedAmount(),
                application.getTermMonths());

        riskEvaluationRepository.save(riskEvaluation);
        application.setRiskEvaluation(riskEvaluation);

        evaluateApplication(application, riskEvaluation);

        return applicationRepository.save(application);
    }

    private void evaluateApplication(CreditApplication application, RiskEvaluation riskEvaluation) {
        Integer score = riskEvaluation.getScore();
        RiskLevel riskLevel = riskEvaluation.getRiskLevel();

        if (score >= AUTO_APPROVE_SCORE_THRESHOLD && !riskEvaluation.isHighRisk()) {
            application.approve();
            return;
        }

        if (score < AUTO_REJECT_SCORE_THRESHOLD) {
            application.reject("Credit score below minimum threshold");
            return;
        }

        if (RiskLevel.ALTO.equals(riskLevel)) {
            if (application.getRequestedAmount().compareTo(HIGH_RISK_MAX_AMOUNT) > 0) {
                application.reject("Amount exceeds limit for high-risk profile");
                return;
            }
            if (application.getTermMonths() > HIGH_RISK_MAX_TERM) {
                application.reject("Term exceeds limit for high-risk profile");
                return;
            }
        }

        application.approve();
    }
}
