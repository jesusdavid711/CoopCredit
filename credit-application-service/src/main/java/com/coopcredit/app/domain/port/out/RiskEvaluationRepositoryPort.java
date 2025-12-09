package com.coopcredit.app.domain.port.out;

import com.coopcredit.app.domain.model.RiskEvaluation;

public interface RiskEvaluationRepositoryPort {
    RiskEvaluation save(RiskEvaluation riskEvaluation);
}
