package com.coopcredit.app.infrastructure.mappers;

import com.coopcredit.app.domain.model.CreditApplication;
import com.coopcredit.app.infrastructure.entities.CreditApplicationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { RiskEvaluationMapper.class })
public interface CreditApplicationMapper {

    @Mapping(target = "affiliateId", source = "affiliate.id")
    CreditApplication toDomain(CreditApplicationEntity entity);

    @Mapping(target = "affiliate", ignore = true)
    @Mapping(target = "riskEvaluation", ignore = true)
    CreditApplicationEntity toEntity(CreditApplication domain);
}
