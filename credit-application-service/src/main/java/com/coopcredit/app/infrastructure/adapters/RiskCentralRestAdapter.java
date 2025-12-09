package com.coopcredit.app.infrastructure.adapters;

import com.coopcredit.app.domain.model.RiskEvaluation;
import com.coopcredit.app.domain.model.enums.RiskLevel;
import com.coopcredit.app.domain.port.out.RiskCentralPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Component
public class RiskCentralRestAdapter implements RiskCentralPort {

    private final RestTemplate restTemplate;
    private final String riskCentralUrl;

    public RiskCentralRestAdapter(RestTemplate restTemplate, @Value("${risk-central.url}") String riskCentralUrl) {
        this.restTemplate = restTemplate;
        this.riskCentralUrl = riskCentralUrl;
    }

    @Override
    public RiskEvaluation evaluateRisk(String documentNumber, BigDecimal amount, Integer termMonths) {
        String url = riskCentralUrl + "/risk-evaluation";

        Map<String, Object> request = Map.of(
                "documento", documentNumber,
                "monto", amount,
                "plazo", termMonths);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);

            if (response == null) {
                throw new RuntimeException("No response from risk central service");
            }

            RiskEvaluation evaluation = new RiskEvaluation();
            evaluation.setDocumentNumber((String) response.get("documento"));
            evaluation.setScore((Integer) response.get("score"));
            evaluation.setRiskLevel(RiskLevel.valueOf((String) response.get("nivelRiesgo")));
            evaluation.setDetail((String) response.get("detalle"));
            evaluation.setEvaluationDate(LocalDateTime.now());

            return evaluation;
        } catch (Exception e) {
            throw new RuntimeException("Error calling risk central service: " + e.getMessage(), e);
        }
    }
}
