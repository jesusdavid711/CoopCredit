package com.coopcredit.app.infrastructure.repositories;

import com.coopcredit.app.infrastructure.entities.CreditApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditApplicationJpaRepository extends JpaRepository<CreditApplicationEntity, Long> {
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = { "affiliate", "riskEvaluation" })
    List<CreditApplicationEntity> findByAffiliateId(Long affiliateId);
}
