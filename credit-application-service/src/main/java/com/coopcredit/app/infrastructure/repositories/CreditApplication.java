package com.coopcredit.app.infrastructure.repositories;

import com.coopcredit.app.infrastructure.entities.CreditApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditApplication JpaRepository extends JpaRepository<CreditApplicationEntity, Long> {
    List<CreditApplicationEntity> findByAffiliateId(Long affiliateId);
}
