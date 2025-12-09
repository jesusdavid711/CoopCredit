package com.coopcredit.app.infrastructure.controllers;

import com.coopcredit.app.domain.model.Affiliate;
import com.coopcredit.app.domain.port.in.GetAffiliateUseCase;
import com.coopcredit.app.domain.port.in.RegisterAffiliateUseCase;
import com.coopcredit.app.domain.port.in.UpdateAffiliateUseCase;
import com.coopcredit.app.infrastructure.mappers.AffiliateMapper;
import com.coopcredit.app.infrastructure.web.dto.AffiliateRequest;
import com.coopcredit.app.infrastructure.web.dto.AffiliateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/affiliates")
@Tag(name = "Affiliates", description = "Gestión de afiliados")
@SecurityRequirement(name = "bearer-jwt")
public class AffiliateController {

    private final RegisterAffiliateUseCase registerAffiliateUseCase;
    private final UpdateAffiliateUseCase updateAffiliateUseCase;
    private final GetAffiliateUseCase getAffiliateUseCase;
    private final AffiliateMapper affiliateMapper;

    public AffiliateController(
            RegisterAffiliateUseCase registerAffiliateUseCase,
            UpdateAffiliateUseCase updateAffiliateUseCase,
            GetAffiliateUseCase getAffiliateUseCase,
            AffiliateMapper affiliateMapper) {
        this.registerAffiliateUseCase = registerAffiliateUseCase;
        this.updateAffiliateUseCase = updateAffiliateUseCase;
        this.getAffiliateUseCase = getAffiliateUseCase;
        this.affiliateMapper = affiliateMapper;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALISTA')")
    @Operation(summary = "Registrar nuevo afiliado")
    public ResponseEntity<AffiliateResponse> register(@Valid @RequestBody AffiliateRequest request) {
        Affiliate affiliate = affiliateMapper.toDomain(request);
        Affiliate saved = registerAffiliateUseCase.execute(affiliate);
        return ResponseEntity.status(HttpStatus.CREATED).body(affiliateMapper.toResponse(saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALISTA')")
    @Operation(summary = "Actualizar afiliado")
    public ResponseEntity<AffiliateResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody AffiliateRequest request) {
        Affiliate affiliate = affiliateMapper.toDomain(request);
        Affiliate updated = updateAffiliateUseCase.execute(id, affiliate);
        return ResponseEntity.ok(affiliateMapper.toResponse(updated));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALISTA', 'AFILIADO')")
    @Operation(summary = "Obtener afiliado por ID")
    public ResponseEntity<AffiliateResponse> getById(@PathVariable Long id) {
        Affiliate affiliate = getAffiliateUseCase.execute(id);
        return ResponseEntity.ok(affiliateMapper.toResponse(affiliate));
    }

    @GetMapping("/document/{documentNumber}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALISTA')")
    @Operation(summary = "Obtener afiliado por número de documento")
    public ResponseEntity<AffiliateResponse> getByDocument(@PathVariable String documentNumber) {
        Affiliate affiliate = getAffiliateUseCase.getByDocumentNumber(documentNumber);
        return ResponseEntity.ok(affiliateMapper.toResponse(affiliate));
    }
}
