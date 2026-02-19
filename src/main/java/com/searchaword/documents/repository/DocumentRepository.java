package com.searchaword.documents.repository;

import com.searchaword.documents.domain.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {

    Optional<DocumentEntity> findBySha256(String sha256);

    List<DocumentEntity> findAllByOrderByCreatedAtDesc();

    Optional<DocumentEntity> findById(Long id);
}