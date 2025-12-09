package com.riskcentral.app.service;

import com.riskcentral.app.dto.RiskEvaluationRequest;
import com.riskcentral.app.dto.RiskEvaluationResponse;
import org.springframework.stereotype.Service;

@Service
public class RiskCalculationService {

    public RiskEvaluationResponse evaluateRisk(RiskEvaluationRequest request) {
        String documento = request.getDocumento();

        // Generate consistent seed from document hash
        int seed = Math.abs(documento.hashCode()) % 1000;

        // Generate score between 300 and 950
        int score = 300 + (seed % 651);

        // Determine risk level based on score
        String riskLevel;
        String detail;

        if (score >= 701) {
            riskLevel = "BAJO";
            detail = "Historial crediticio excelente.";
        } else if (score >= 501) {
            riskLevel = "MEDIO";
            detail = "Historial crediticio moderado.";
        } else {
            riskLevel = "ALTO";
            detail = "Historial crediticio con riesgos.";
        }

        return new RiskEvaluationResponse(documento, score, riskLevel, detail);
    }
}
