package com.coopcredit.app.domain.port.in;

import com.coopcredit.app.domain.model.CreditApplication;

public interface RegisterCreditApplicationUseCase {
    CreditApplication execute(CreditApplication application);
}
