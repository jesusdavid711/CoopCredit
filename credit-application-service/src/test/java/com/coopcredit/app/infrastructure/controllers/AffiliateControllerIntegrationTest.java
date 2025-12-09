package com.coopcredit.app.infrastructure.controllers;

import com.coopcredit.app.domain.model.Affiliate;
import com.coopcredit.app.domain.model.enums.AffiliateStatus;
import com.coopcredit.app.domain.port.in.GetAffiliateUseCase;
import com.coopcredit.app.domain.port.in.RegisterAffiliateUseCase;
import com.coopcredit.app.infrastructure.mappers.AffiliateMapper;
import com.coopcredit.app.infrastructure.web.dto.AffiliateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AffiliateController.class)
class AffiliateControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RegisterAffiliateUseCase registerAffiliateUseCase;

    @MockBean
    private GetAffiliateUseCase getAffiliateUseCase;

    @MockBean
    private AffiliateMapper affiliateMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testRegisterAffiliate_WithValidData_ReturnsCreated() throws Exception {
        AffiliateRequest request = createAffiliateRequest();
        Affiliate affiliate = createAffiliate();

        when(affiliateMapper.toDomain(any(AffiliateRequest.class))).thenReturn(affiliate);
        when(registerAffiliateUseCase.execute(any(Affiliate.class))).thenReturn(affiliate);
        when(affiliateMapper.toResponse(any(Affiliate.class))).thenReturn(null);

        mockMvc.perform(post("/api/affiliates")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "AFILIADO")
    void testRegisterAffiliate_WithoutPermission_ReturnsForbidden() throws Exception {
        AffiliateRequest request = createAffiliateRequest();

        mockMvc.perform(post("/api/affiliates")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testRegisterAffiliate_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
        AffiliateRequest request = createAffiliateRequest();

        mockMvc.perform(post("/api/affiliates")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAffiliate_WithValidId_ReturnsOk() throws Exception {
        Affiliate affiliate = createAffiliate();

        when(getAffiliateUseCase.execute(1L)).thenReturn(affiliate);
        when(affiliateMapper.toResponse(any(Affiliate.class))).thenReturn(null);

        mockMvc.perform(get("/api/affiliates/1")
                .with(csrf()))
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
