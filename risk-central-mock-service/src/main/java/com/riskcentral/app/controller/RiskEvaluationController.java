package com.riskcentral.app.controller;

import com.riskcentral.app.dto.RiskEvaluationRequest;
import com.riskcentral.app.dto.RiskEvaluationResponse;
import com.riskcentral.app.service.RiskCalculationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/risk-evaluation")
public class RiskEvaluationController {

    private final RiskCalculationService riskCalculationService;

    public RiskEvaluationController(RiskCalculationService riskCalculationService) {
        this.riskCalculationService = riskCalculationService;
    }

    @PostMapping
    public ResponseEntity<RiskEvaluationResponse> evaluateRisk(@RequestBody RiskEvaluationRequest request) {
        RiskEvaluationResponse response = riskCalculationService.evaluateRisk(request);
        return ResponseEntity.ok(response);
    }
}
