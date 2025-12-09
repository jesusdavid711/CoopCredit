package com.coopcredit.app.infrastructure.mappers;

import com.coopcredit.app.domain.model.CreditApplication;
import com.coopcredit.app.infrastructure.entities.CreditApplicationEntity;
import com.coopcredit.app.infrastructure.web.dto.CreditApplicationRequest;
import com.coopcredit.app.infrastructure.web.dto.CreditApplicationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { RiskEvaluationMapper.class })
public interface CreditApplicationMapper {

    @Mapping(target = "affiliateId", source = "affiliate.id")
    CreditApplication toDomain(CreditApplicationEntity entity);

    @Mapping(target = "affiliate", ignore = true)
    @Mapping(target = "riskEvaluation", ignore = true)
    CreditApplicationEntity toEntity(CreditApplication domain);

    CreditApplication toDomain(CreditApplicationRequest request);

    @Mapping(target = "riskEvaluation.score", source = "riskEvaluation.score")
    @Mapping(target = "riskEvaluation.riskLevel", source = "riskEvaluation.riskLevel")
    @Mapping(target = "riskEvaluation.detail", source = "riskEvaluation.detail")
    CreditApplicationResponse toResponse(CreditApplication domain);
}
