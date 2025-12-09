package com.coopcredit.app.infrastructure.repositories;

import com.coopcredit.app.infrastructure.entities.AffiliateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AffiliateJpaRepository extends JpaRepository<AffiliateEntity, Long> {
    Optional<AffiliateEntity> findByDocumentNumber(String documentNumber);

    boolean existsByDocumentNumber(String documentNumber);
}
