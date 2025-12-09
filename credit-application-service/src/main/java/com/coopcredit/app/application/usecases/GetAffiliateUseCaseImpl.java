package com.coopcredit.app.application.usecases;

import com.coopcredit.app.domain.exception.AffiliateNotFoundException;
import com.coopcredit.app.domain.model.Affiliate;
import com.coopcredit.app.domain.port.in.GetAffiliateUseCase;
import com.coopcredit.app.domain.port.out.AffiliateRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetAffiliateUseCaseImpl implements GetAffiliateUseCase {

    private final AffiliateRepositoryPort affiliateRepository;

    public GetAffiliateUseCaseImpl(AffiliateRepositoryPort affiliateRepository) {
        this.affiliateRepository = affiliateRepository;
    }

    @Override
    public Affiliate execute(Long id) {
        return affiliateRepository.findById(id)
                .orElseThrow(() -> new AffiliateNotFoundException(id));
    }

    @Override
    public Affiliate getByDocumentNumber(String documentNumber) {
        return affiliateRepository.findByDocumentNumber(documentNumber)
                .orElseThrow(() -> new AffiliateNotFoundException(documentNumber));
    }
}
