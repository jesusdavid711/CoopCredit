package com.coopcredit.app.domain.port.out;

import com.coopcredit.app.domain.model.Affiliate;

import java.util.Optional;

public interface AffiliateRepositoryPort {
    Affiliate save(Affiliate affiliate);

    Optional<Affiliate> findById(Long id);

    Optional<Affiliate> findByDocumentNumber(String documentNumber);

    boolean existsByDocumentNumber(String documentNumber);
}
