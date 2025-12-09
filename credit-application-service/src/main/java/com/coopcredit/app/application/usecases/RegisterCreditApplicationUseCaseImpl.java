package com.coopcredit.app.application.usecases;

import com.coopcredit.app.domain.exception.AffiliateNotFoundException;
import com.coopcredit.app.domain.exception.BusinessValidationException;
import com.coopcredit.app.domain.exception.InactiveAffiliateException;
import com.coopcredit.app.domain.model.Affiliate;
import com.coopcredit.app.domain.model.CreditApplication;
import com.coopcredit.app.domain.model.enums.ApplicationStatus;
import com.coopcredit.app.domain.port.in.RegisterCreditApplicationUseCase;
import com.coopcredit.app.domain.port.out.AffiliateRepositoryPort;
import com.coopcredit.app.domain.port.out.CreditApplicationRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Transactional
public class RegisterCreditApplicationUseCaseImpl implements RegisterCreditApplicationUseCase {

    private static final int MINIMUM_SENIORITY_MONTHS = 6;
    private static final int SALARY_MULTIPLIER = 5;
    private static final BigDecimal MAX_DEBT_TO_INCOME_RATIO = new BigDecimal("0.40");

    private final CreditApplicationRepositoryPort applicationRepository;
    private final AffiliateRepositoryPort affiliateRepository;

    public RegisterCreditApplicationUseCaseImpl(
            CreditApplicationRepositoryPort applicationRepository,
            AffiliateRepositoryPort affiliateRepository) {
        this.applicationRepository = applicationRepository;
        this.affiliateRepository = affiliateRepository;
    }

    @Override
    public CreditApplication execute(CreditApplication application) {
        Affiliate affiliate = affiliateRepository.findById(application.getAffiliateId())
                .orElseThrow(() -> new AffiliateNotFoundException(application.getAffiliateId()));

        validateAffiliate(affiliate);
        validateCreditAmount(affiliate, application);
        validateDebtToIncomeRatio(affiliate, application);

        application.setApplicationDate(LocalDateTime.now());
        application.setStatus(ApplicationStatus.PENDING);

        return applicationRepository.save(application);
    }

    private void validateAffiliate(Affiliate affiliate) {
        if (!affiliate.isActive()) {
            throw new InactiveAffiliateException(affiliate.getDocumentNumber());
        }

        if (!affiliate.hasMinimumSeniority(MINIMUM_SENIORITY_MONTHS)) {
            throw new BusinessValidationException(
                    "Affiliate must have at least " + MINIMUM_SENIORITY_MONTHS + " months of seniority");
        }
    }

    private void validateCreditAmount(Affiliate affiliate, CreditApplication application) {
        BigDecimal maxAmount = affiliate.getMaxCreditAmount(SALARY_MULTIPLIER);
        if (application.getRequestedAmount().compareTo(maxAmount) > 0) {
            throw new BusinessValidationException(
                    "Requested amount exceeds maximum allowed: " + maxAmount);
        }
    }

    private void validateDebtToIncomeRatio(Affiliate affiliate, CreditApplication application) {
        BigDecimal monthlyPayment = application.calculateMonthlyPayment();
        BigDecimal debtToIncomeRatio = monthlyPayment.divide(affiliate.getSalary(), 4, BigDecimal.ROUND_HALF_UP);

        if (debtToIncomeRatio.compareTo(MAX_DEBT_TO_INCOME_RATIO) > 0) {
            throw new BusinessValidationException(
                    "Monthly payment exceeds 40% of salary. Debt-to-income ratio: " +
                            debtToIncomeRatio.multiply(BigDecimal.valueOf(100)) + "%");
        }
    }
}
