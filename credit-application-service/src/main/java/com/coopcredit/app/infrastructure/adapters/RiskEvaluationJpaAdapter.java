package com.coopcredit.app.infrastructure.adapters;

import com.coopcredit.app.domain.model.RiskEvaluation;
import com.coopcredit.app.domain.port.out.RiskEvaluationRepositoryPort;
import com.coopcredit.app.infrastructure.mappers.RiskEvaluationMapper;
import com.coopcredit.app.infrastructure.repositories.RiskEvaluationJpaRepository;
import org.springframework.stereotype.Component;

@Component
public class RiskEvaluationJpaAdapter implements RiskEvaluationRepositoryPort {

    private final RiskEvaluationJpaRepository repository;
    private final RiskEvaluationMapper mapper;

    public RiskEvaluationJpaAdapter(RiskEvaluationJpaRepository repository, RiskEvaluationMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public RiskEvaluation save(RiskEvaluation riskEvaluation) {
        return mapper.toDomain(repository.save(mapper.toEntity(riskEvaluation)));
    }
}
