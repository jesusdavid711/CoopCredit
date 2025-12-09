package com.coopcredit.app.domain.port.in;

import com.coopcredit.app.domain.model.CreditApplication;

public interface EvaluateCreditApplicationUseCase {
    CreditApplication execute(Long applicationId);
}
