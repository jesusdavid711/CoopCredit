package com.coopcredit.app.application.usecases;

import com.coopcredit.app.domain.exception.DuplicateDocumentException;
import com.coopcredit.app.domain.model.Affiliate;
import com.coopcredit.app.domain.model.enums.AffiliateStatus;
import com.coopcredit.app.domain.port.out.AffiliateRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterAffiliateUseCaseImplTest {

    @Mock
    private AffiliateRepositoryPort affiliateRepository;

    @InjectMocks
    private RegisterAffiliateUseCaseImpl registerAffiliateUseCase;

    private Affiliate affiliate;

    @BeforeEach
    void setUp() {
        affiliate = createAffiliate();
    }

    @Test
    void execute_WithNewAffiliate_SavesSuccessfully() {
        when(affiliateRepository.existsByDocumentNumber(affiliate.getDocumentNumber())).thenReturn(false);
        when(affiliateRepository.save(any(Affiliate.class))).thenReturn(affiliate);

        Affiliate result = registerAffiliateUseCase.execute(affiliate);

        assertNotNull(result);
        assertEquals(affiliate.getDocumentNumber(), result.getDocumentNumber());
        verify(affiliateRepository).existsByDocumentNumber(affiliate.getDocumentNumber());
        verify(affiliateRepository).save(affiliate);
    }

    @Test
    void execute_WithDuplicateDocument_ThrowsException() {
        when(affiliateRepository.existsByDocumentNumber(affiliate.getDocumentNumber())).thenReturn(true);

        assertThrows(DuplicateDocumentException.class, () -> {
            registerAffiliateUseCase.execute(affiliate);
        });

        verify(affiliateRepository).existsByDocumentNumber(affiliate.getDocumentNumber());
        verify(affiliateRepository, never()).save(any());
    }

    @Test
    void execute_VerifiesDocumentBeforeSaving() {
        when(affiliateRepository.existsByDocumentNumber(anyString())).thenReturn(false);
        when(affiliateRepository.save(any(Affiliate.class))).thenReturn(affiliate);

        registerAffiliateUseCase.execute(affiliate);

        var inOrder = inOrder(affiliateRepository);
        inOrder.verify(affiliateRepository).existsByDocumentNumber(affiliate.getDocumentNumber());
        inOrder.verify(affiliateRepository).save(affiliate);
    }

    private Affiliate createAffiliate() {
        Affiliate aff = new Affiliate();
        aff.setDocumentNumber("12345678");
        aff.setName("Test Affiliate");
        aff.setSalary(new BigDecimal("3000000"));
        aff.setAffiliationDate(LocalDate.now());
        aff.setStatus(AffiliateStatus.ACTIVE);
        return aff;
    }
}
