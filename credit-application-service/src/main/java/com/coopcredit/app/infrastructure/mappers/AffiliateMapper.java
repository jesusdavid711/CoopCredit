package com.coopcredit.app.infrastructure.mappers;

import com.coopcredit.app.domain.model.Affiliate;
import com.coopcredit.app.infrastructure.entities.AffiliateEntity;
import com.coopcredit.app.infrastructure.web.dto.AffiliateRequest;
import com.coopcredit.app.infrastructure.web.dto.AffiliateResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AffiliateMapper {

    Affiliate toDomain(AffiliateEntity entity);

    @Mapping(target = "applications", ignore = true)
    AffiliateEntity toEntity(Affiliate domain);

    Affiliate toDomain(AffiliateRequest request);

    AffiliateResponse toResponse(Affiliate domain);
}
