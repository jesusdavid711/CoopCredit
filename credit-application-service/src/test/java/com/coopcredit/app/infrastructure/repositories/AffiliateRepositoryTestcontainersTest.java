package com.coopcredit.app.infrastructure.repositories;

import com.coopcredit.app.domain.model.enums.AffiliateStatus;
import com.coopcredit.app.infrastructure.entities.AffiliateEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AffiliateRepositoryTestcontainersTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> "false");
    }

    @Autowired
    private AffiliateJpaRepository affiliateRepository;

    @Test
    void testPostgresIsRunning() {
        assertTrue(postgres.isRunning());
    }

    @Test
    void testSaveAffiliate_PersistsSuccessfully() {
        AffiliateEntity affiliate = createAffiliateEntity();

        AffiliateEntity saved = affiliateRepository.save(affiliate);

        assertNotNull(saved.getId());
        assertEquals("12345678", saved.getDocumentNumber());
    }

    @Test
    void testFindById_ReturnsAffiliate() {
        AffiliateEntity affiliate = createAffiliateEntity();
        AffiliateEntity saved = affiliateRepository.save(affiliate);

        Optional<AffiliateEntity> found = affiliateRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(saved.getDocumentNumber(), found.get().getDocumentNumber());
    }

    @Test
    void testFindByDocumentNumber_ReturnsAffiliate() {
        AffiliateEntity affiliate = createAffiliateEntity();
        affiliateRepository.save(affiliate);

        Optional<AffiliateEntity> found = affiliateRepository.findByDocumentNumber("12345678");

        assertTrue(found.isPresent());
        assertEquals("Test Affiliate", found.get().getName());
    }

    @Test
    void testExistsByDocumentNumber_ReturnsTrue() {
        AffiliateEntity affiliate = createAffiliateEntity();
        affiliateRepository.save(affiliate);

        boolean exists = affiliateRepository.existsByDocumentNumber("12345678");

        assertTrue(exists);
    }

    @Test
    void testExistsByDocumentNumber_ReturnsFalse() {
        boolean exists = affiliateRepository.existsByDocumentNumber("99999999");

        assertFalse(exists);
    }

    @Test
    void testUpdateAffiliate_UpdatesSuccessfully() {
        AffiliateEntity affiliate = createAffiliateEntity();
        AffiliateEntity saved = affiliateRepository.save(affiliate);

        saved.setName("Updated Name");
        saved.setStatus(AffiliateStatus.INACTIVE);
        AffiliateEntity updated = affiliateRepository.save(saved);

        assertEquals("Updated Name", updated.getName());
        assertEquals(AffiliateStatus.INACTIVE, updated.getStatus());
    }

    @Test
    void testDeleteAffiliate_DeletesSuccessfully() {
        AffiliateEntity affiliate = createAffiliateEntity();
        AffiliateEntity saved = affiliateRepository.save(affiliate);
        Long id = saved.getId();

        affiliateRepository.deleteById(id);

        Optional<AffiliateEntity> found = affiliateRepository.findById(id);
        assertFalse(found.isPresent());
    }

    private AffiliateEntity createAffiliateEntity() {
        AffiliateEntity affiliate = new AffiliateEntity();
        affiliate.setDocumentNumber("12345678");
        affiliate.setName("Test Affiliate");
        affiliate.setSalary(new BigDecimal("3000000"));
        affiliate.setAffiliationDate(LocalDate.now());
        affiliate.setStatus(AffiliateStatus.ACTIVE);
        return affiliate;
    }
}
