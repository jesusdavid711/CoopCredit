package com.coopcredit.app.infrastructure.controllers;

import com.coopcredit.app.domain.model.Affiliate;
import com.coopcredit.app.domain.model.enums.AffiliateStatus;
import com.coopcredit.app.domain.port.in.GetAffiliateUseCase;
import com.coopcredit.app.domain.port.in.RegisterAffiliateUseCase;
import com.coopcredit.app.domain.port.in.UpdateAffiliateUseCase;
import com.coopcredit.app.infrastructure.mappers.AffiliateMapper;
import com.coopcredit.app.infrastructure.web.dto.AffiliateRequest;
import com.coopcredit.app.infrastructure.web.dto.AffiliateResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AffiliateControllerIntegrationTest {

    private MockMvc mockMvc;

    @Mock
    private RegisterAffiliateUseCase registerAffiliateUseCase;

    @Mock
    private UpdateAffiliateUseCase updateAffiliateUseCase;

    @Mock
    private GetAffiliateUseCase getAffiliateUseCase;

    @Mock
    private AffiliateMapper affiliateMapper;

    @InjectMocks
    private AffiliateController affiliateController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(affiliateController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @Test
    void testRegisterAffiliate_WithValidData_ReturnsCreated() throws Exception {
        AffiliateRequest request = createAffiliateRequest();
        Affiliate affiliate = createAffiliate();
        AffiliateResponse response = new AffiliateResponse();

        when(affiliateMapper.toDomain(any(AffiliateRequest.class))).thenReturn(affiliate);
        when(registerAffiliateUseCase.execute(any(Affiliate.class))).thenReturn(affiliate);
        when(affiliateMapper.toResponse(any(Affiliate.class))).thenReturn(response);

        mockMvc.perform(post("/api/affiliates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void testGetAffiliate_WithValidId_ReturnsOk() throws Exception {
        Affiliate affiliate = createAffiliate();
        AffiliateResponse response = new AffiliateResponse();

        when(getAffiliateUseCase.execute(anyLong())).thenReturn(affiliate);
        when(affiliateMapper.toResponse(any(Affiliate.class))).thenReturn(response);

        mockMvc.perform(get("/api/affiliates/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private AffiliateRequest createAffiliateRequest() {
        AffiliateRequest request = new AffiliateRequest();
        request.setDocumentNumber("12345678");
        request.setName("Test Affiliate");
        request.setSalary(new BigDecimal("3000000"));
        request.setAffiliationDate(LocalDate.now());
        request.setStatus(AffiliateStatus.ACTIVE);
        return request;
    }

    private Affiliate createAffiliate() {
        Affiliate affiliate = new Affiliate();
        affiliate.setId(1L);
        affiliate.setDocumentNumber("12345678");
        affiliate.setName("Test Affiliate");
        affiliate.setSalary(new BigDecimal("3000000"));
        affiliate.setAffiliationDate(LocalDate.now());
        affiliate.setStatus(AffiliateStatus.ACTIVE);
        return affiliate;
    }
}
