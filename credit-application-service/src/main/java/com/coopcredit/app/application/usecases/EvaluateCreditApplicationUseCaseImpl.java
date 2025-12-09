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
import java.math.RoundingMode;

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

    /**
     * Executes the credit application evaluation process.
     * <p>
     * This method orchestrates the following steps:
     * 1. Validates existence of application and affiliate.
     * 2. Calculates debt-to-income ratio (must be < 40%).
     * 3. Validates maximum credit amount based on salary (max 5x).
     * 4. Validates minimum seniority (6 months).
     * 5. Calls external Risk Central service to get score.
     * 6. Determines final status based on score and risk level.
     * </p>
     *
     * @param applicationId The ID of the application to evaluate.
     * @return The updated CreditApplication with the final status.
     */
    @Override
    public CreditApplication execute(Long applicationId) {
        // 1. Retrieve Application and Affiliate
        CreditApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found"));

        var affiliate = affiliateRepository.findById(application.getAffiliateId())
                .orElseThrow(() -> new BusinessValidationException("Affiliate not found"));

        // 2. Policy: Debt-to-Income Ratio Check (< 40%)
        BigDecimal monthlyPayment = application.calculateMonthlyPayment();
        BigDecimal monthlyIncome = affiliate.getSalary();
        BigDecimal debtToIncomeRatio = monthlyIncome.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO // Avoid division by zero
                : monthlyPayment.divide(monthlyIncome, 4, RoundingMode.HALF_UP);

        if (debtToIncomeRatio.compareTo(new BigDecimal("0.40")) > 0) {
            application.reject("Debt-to-income ratio exceeds 40%");
            return applicationRepository.save(application);
        }

        // 3. Policy: Maximum Amount Check (5x Salary)
        BigDecimal maxAmount = affiliate.getMaxCreditAmount(5);
        if (application.getRequestedAmount().compareTo(maxAmount) > 0) {
            application.reject("Amount exceeds maximum allowed based on salary");
            return applicationRepository.save(application);
        }

        // 4. Policy: Minimum Seniority Check (6 months)
        if (!affiliate.hasMinimumSeniority(6)) {
            application.reject("Insufficient seniority (minimum 6 months required)");
            return applicationRepository.save(application);
        }

        // 5. External Risk Evaluation
        RiskEvaluation riskEvaluation = riskCentralPort.evaluateRisk(
                affiliate.getDocumentNumber(),
                application.getRequestedAmount(),
                application.getTermMonths());

        // Persist the risk evaluation result
        riskEvaluation.setCreditApplication(application);
        riskEvaluationRepository.save(riskEvaluation);
        application.setRiskEvaluation(riskEvaluation);

        // 6. Final Decision Logic based on Score
        int score = riskEvaluation.getScore();
        if (score >= 300) {
            // Additional check for High Risk with large amounts
            if (riskEvaluation.getRiskLevel() == RiskLevel.ALTO &&
                    application.getRequestedAmount().compareTo(new BigDecimal("5000000")) > 0) {
                application.reject("High risk level for requested amount");
            } else {
                application.approve();
            }
        } else {
            application.reject("Credit score below minimum threshold");
        }

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
