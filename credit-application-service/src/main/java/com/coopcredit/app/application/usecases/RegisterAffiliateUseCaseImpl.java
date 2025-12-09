package com.coopcredit.app.application.usecases;

import com.coopcredit.app.domain.exception.DuplicateDocumentException;
import com.coopcredit.app.domain.model.Affiliate;
import com.coopcredit.app.domain.port.in.RegisterAffiliateUseCase;
import com.coopcredit.app.domain.port.out.AffiliateRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RegisterAffiliateUseCaseImpl implements RegisterAffiliateUseCase {

    private final AffiliateRepositoryPort affiliateRepository;

    public RegisterAffiliateUseCaseImpl(AffiliateRepositoryPort affiliateRepository) {
        this.affiliateRepository = affiliateRepository;
    }

    @Override
    public Affiliate execute(Affiliate affiliate) {
        if (affiliateRepository.existsByDocumentNumber(affiliate.getDocumentNumber())) {
            throw new DuplicateDocumentException(affiliate.getDocumentNumber());
        }
        return affiliateRepository.save(affiliate);
    }
}
