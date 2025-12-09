package com.coopcredit.app.domain.port.in;

import com.coopcredit.app.domain.model.CreditApplication;

import java.util.List;

public interface GetApplicationsByAffiliateUseCase {
    List<CreditApplication> execute(Long affiliateId);
}
