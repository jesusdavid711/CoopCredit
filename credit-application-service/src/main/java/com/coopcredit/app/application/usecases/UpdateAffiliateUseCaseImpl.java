package com.coopcredit.app.application.usecases;

import com.coopcredit.app.domain.exception.AffiliateNotFoundException;
import com.coopcredit.app.domain.model.Affiliate;
import com.coopcredit.app.domain.port.in.UpdateAffiliateUseCase;
import com.coopcredit.app.domain.port.out.AffiliateRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UpdateAffiliateUseCaseImpl implements UpdateAffiliateUseCase {

    private final AffiliateRepositoryPort affiliateRepository;

    public UpdateAffiliateUseCaseImpl(AffiliateRepositoryPort affiliateRepository) {
        this.affiliateRepository = affiliateRepository;
    }

    @Override
    public Affiliate execute(Long id, Affiliate affiliate) {
        Affiliate existing = affiliateRepository.findById(id)
                .orElseThrow(() -> new AffiliateNotFoundException(id));

        existing.setName(affiliate.getName());
        existing.setSalary(affiliate.getSalary());
        existing.setStatus(affiliate.getStatus());

        return affiliateRepository.save(existing);
    }
}
