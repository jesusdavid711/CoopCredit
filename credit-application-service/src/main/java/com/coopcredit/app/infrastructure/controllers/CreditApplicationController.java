package com.coopcredit.app.infrastructure.controllers;

import com.coopcredit.app.domain.model.CreditApplication;
import com.coopcredit.app.domain.port.in.EvaluateCreditApplicationUseCase;
import com.coopcredit.app.domain.port.in.GetApplicationsByAffiliateUseCase;
import com.coopcredit.app.domain.port.in.RegisterCreditApplicationUseCase;
import com.coopcredit.app.infrastructure.mappers.CreditApplicationMapper;
import com.coopcredit.app.infrastructure.web.dto.CreditApplicationRequest;
import com.coopcredit.app.infrastructure.web.dto.CreditApplicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/credit-applications")
@Tag(name = "Credit Applications", description = "Gestión de solicitudes de crédito")
@SecurityRequirement(name = "bearer-jwt")
public class CreditApplicationController {

    private final RegisterCreditApplicationUseCase registerCreditApplicationUseCase;
    private final EvaluateCreditApplicationUseCase evaluateCreditApplicationUseCase;
    private final GetApplicationsByAffiliateUseCase getApplicationsByAffiliateUseCase;
    private final CreditApplicationMapper creditApplicationMapper;

    public CreditApplicationController(
            RegisterCreditApplicationUseCase registerCreditApplicationUseCase,
            EvaluateCreditApplicationUseCase evaluateCreditApplicationUseCase,
            GetApplicationsByAffiliateUseCase getApplicationsByAffiliateUseCase,
            CreditApplicationMapper creditApplicationMapper) {
        this.registerCreditApplicationUseCase = registerCreditApplicationUseCase;
        this.evaluateCreditApplicationUseCase = evaluateCreditApplicationUseCase;
        this.getApplicationsByAffiliateUseCase = getApplicationsByAffiliateUseCase;
        this.creditApplicationMapper = creditApplicationMapper;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALISTA', 'AFILIADO')")
    @Operation(summary = "Crear nueva solicitud de crédito")
    public ResponseEntity<CreditApplicationResponse> create(@Valid @RequestBody CreditApplicationRequest request) {
        CreditApplication application = creditApplicationMapper.toDomain(request);
        CreditApplication saved = registerCreditApplicationUseCase.execute(application);
        return ResponseEntity.status(HttpStatus.CREATED).body(creditApplicationMapper.toResponse(saved));
    }

    @PostMapping("/{id}/evaluate")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALISTA')")
    @Operation(summary = "Evaluar solicitud de crédito")
    public ResponseEntity<CreditApplicationResponse> evaluate(@PathVariable Long id) {
        CreditApplication evaluated = evaluateCreditApplicationUseCase.execute(id);
        return ResponseEntity.ok(creditApplicationMapper.toResponse(evaluated));
    }

    @GetMapping("/affiliate/{affiliateId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALISTA', 'AFILIADO')")
    @Operation(summary = "Obtener solicitudes por afiliado")
    public ResponseEntity<List<CreditApplicationResponse>> getByAffiliate(@PathVariable Long affiliateId) {
        List<CreditApplication> applications = getApplicationsByAffiliateUseCase.execute(affiliateId);
        List<CreditApplicationResponse> responses = applications.stream()
                .map(creditApplicationMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }
}
