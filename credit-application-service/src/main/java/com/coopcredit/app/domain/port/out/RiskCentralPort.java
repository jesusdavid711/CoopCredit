package com.coopcredit.app.domain.port.out;

import com.coopcredit.app.domain.model.RiskEvaluation;

import java.math.BigDecimal;

public interface RiskCentralPort {
    RiskEvaluation evaluateRisk(String documentNumber, BigDecimal amount, Integer termMonths);
}
