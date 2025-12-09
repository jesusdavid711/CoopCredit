package com.coopcredit.app.infrastructure.adapters;

import com.coopcredit.app.domain.model.CreditApplication;
import com.coopcredit.app.domain.port.out.CreditApplicationRepositoryPort;
import com.coopcredit.app.infrastructure.entities.AffiliateEntity;
import com.coopcredit.app.infrastructure.entities.CreditApplicationEntity;
import com.coopcredit.app.infrastructure.mappers.CreditApplicationMapper;
import com.coopcredit.app.infrastructure.repositories.AffiliateJpaRepository;
import com.coopcredit.app.infrastructure.repositories.CreditApplicationJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CreditApplicationJpaAdapter implements CreditApplicationRepositoryPort {

    private final CreditApplicationJpaRepository repository;
    private final AffiliateJpaRepository affiliateRepository;
    private final CreditApplicationMapper mapper;

    public CreditApplicationJpaAdapter(
            CreditApplicationJpaRepository repository,
            AffiliateJpaRepository affiliateRepository,
            CreditApplicationMapper mapper) {
        this.repository = repository;
        this.affiliateRepository = affiliateRepository;
        this.mapper = mapper;
    }

    @Override
    public CreditApplication save(CreditApplication application) {
        CreditApplicationEntity entity = mapper.toEntity(application);

        if (application.getAffiliateId() != null) {
            AffiliateEntity affiliate = affiliateRepository.findById(application.getAffiliateId())
                    .orElseThrow(() -> new RuntimeException("Affiliate not found"));
            entity.setAffiliate(affiliate);
        }

        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<CreditApplication> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<CreditApplication> findByAffiliateId(Long affiliateId) {
        return repository.findByAffiliateId(affiliateId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
