package com.coopcredit.app.infrastructure.adapters;

import com.coopcredit.app.domain.model.Affiliate;
import com.coopcredit.app.domain.port.out.AffiliateRepositoryPort;
import com.coopcredit.app.infrastructure.mappers.AffiliateMapper;
import com.coopcredit.app.infrastructure.repositories.AffiliateJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AffiliateJpaAdapter implements AffiliateRepositoryPort {

    private final AffiliateJpaRepository repository;
    private final AffiliateMapper mapper;

    public AffiliateJpaAdapter(AffiliateJpaRepository repository, AffiliateMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Affiliate save(Affiliate affiliate) {
        return mapper.toDomain(repository.save(mapper.toEntity(affiliate)));
    }

    @Override
    public Optional<Affiliate> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Affiliate> findByDocumentNumber(String documentNumber) {
        return repository.findByDocumentNumber(documentNumber).map(mapper::toDomain);
    }

    @Override
    public boolean existsByDocumentNumber(String documentNumber) {
        return repository.existsByDocumentNumber(documentNumber);
    }
}
