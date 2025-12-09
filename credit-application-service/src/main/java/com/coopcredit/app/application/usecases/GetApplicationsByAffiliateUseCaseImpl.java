package com.coopcredit.app.application.usecases;

import com.coopcredit.app.domain.model.CreditApplication;
import com.coopcredit.app.domain.port.in.GetApplicationsByAffiliateUseCase;
import com.coopcredit.app.domain.port.out.CreditApplicationRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GetApplicationsByAffiliateUseCaseImpl implements GetApplicationsByAffiliateUseCase {

    private final CreditApplicationRepositoryPort applicationRepository;

    public GetApplicationsByAffiliateUseCaseImpl(CreditApplicationRepositoryPort applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    @Override
    public List<CreditApplication> execute(Long affiliateId) {
        return applicationRepository.findByAffiliateId(affiliateId);
    }
}
