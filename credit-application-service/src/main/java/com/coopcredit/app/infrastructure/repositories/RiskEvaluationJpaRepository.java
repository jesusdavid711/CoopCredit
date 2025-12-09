package com.coopcredit.app.infrastructure.repositories;

import com.coopcredit.app.infrastructure.entities.RiskEvaluationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RiskEvaluationJpaRepository extends JpaRepository<RiskEvaluationEntity, Long> {
}
