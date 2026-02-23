package com.searchaword.documents.repository;

import com.searchaword.documents.domain.DocumentEntity;
import com.searchaword.security.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {

    // ============================================
    // Owner-aware SHA256 lookup
    // ============================================

    Optional<DocumentEntity> findBySha256AndOwner(String sha256, User owner);

    // ============================================
    // Owner-aware listing
    // ============================================

    List<DocumentEntity> findAllByOwnerOrderByCreatedAtDesc(User owner);

    // ============================================
    // Owner-aware ID lookup
    // ============================================

    Optional<DocumentEntity> findByIdAndOwner(Long id, User owner);
}