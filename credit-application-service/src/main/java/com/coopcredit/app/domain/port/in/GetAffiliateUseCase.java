package com.coopcredit.app.domain.port.in;

import com.coopcredit.app.domain.model.Affiliate;

public interface GetAffiliateUseCase {
    Affiliate execute(Long id);

    Affiliate getByDocumentNumber(String documentNumber);
}
