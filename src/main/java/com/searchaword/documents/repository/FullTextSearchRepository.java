package com.searchaword.documents.repository;

import com.searchaword.documents.domain.DocumentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FullTextSearchRepository extends JpaRepository<DocumentEntity, Long> {

    @Query(value = """
            SELECT 
                d.id,
                d.original_file_name,
                ts_rank(d.search_vector, plainto_tsquery('english', :query)) AS rank,
                ts_headline('english', d.extracted_text, plainto_tsquery('english', :query)) AS snippet
            FROM documents d
            WHERE d.user_id = :userId
              AND d.search_vector @@ plainto_tsquery('english', :query)
            ORDER BY rank DESC
            """,
            countQuery = """
            SELECT COUNT(*)
            FROM documents d
            WHERE d.user_id = :userId
              AND d.search_vector @@ plainto_tsquery('english', :query)
            """,
            nativeQuery = true)
    Page<Object[]> searchDocuments(
            @Param("query") String query,
            @Param("userId") Long userId,
            Pageable pageable
    );
}