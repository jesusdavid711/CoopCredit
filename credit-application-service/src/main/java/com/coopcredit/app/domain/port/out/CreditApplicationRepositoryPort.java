package com.coopcredit.app.domain.port.out;

import com.coopcredit.app.domain.model.CreditApplication;

import java.util.List;
import java.util.Optional;

public interface CreditApplicationRepositoryPort {
    CreditApplication save(CreditApplication application);

    Optional<CreditApplication> findById(Long id);

    List<CreditApplication> findByAffiliateId(Long affiliateId);
}
