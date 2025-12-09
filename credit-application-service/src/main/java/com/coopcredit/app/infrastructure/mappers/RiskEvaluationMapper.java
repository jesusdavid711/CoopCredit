package com.coopcredit.app.infrastructure.mappers;

import com.coopcredit.app.domain.model.RiskEvaluation;
import com.coopcredit.app.infrastructure.entities.RiskEvaluationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RiskEvaluationMapper {

    RiskEvaluation toDomain(RiskEvaluationEntity entity);

    @Mapping(target = "creditApplication", ignore = true)
    RiskEvaluationEntity toEntity(RiskEvaluation domain);
}
