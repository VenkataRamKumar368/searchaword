package com.searchaword.searchhistory;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "search_history")
public class SearchHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "document_id")
    private Long documentId;

    @Column(name = "query_text", nullable = false, length = 255)
    private String queryText;

    @Enumerated(EnumType.STRING)
    @Column(name = "query_type", nullable = false, length = 30)
    private SearchQueryType queryType;

    @Column(name = "match_count", nullable = false)
    private Integer matchCount = 0;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    // ==============================
    // Constructors
    // ==============================

    public SearchHistoryEntity() {
    }

    public SearchHistoryEntity(Long userId,
                               Long documentId,
                               String queryText,
                               SearchQueryType queryType,
                               Integer matchCount) {
        this.userId = userId;
        this.documentId = documentId;
        this.queryText = queryText;
        this.queryType = queryType;
        this.matchCount = matchCount;
    }

    // ==============================
    // Lifecycle Hooks
    // ==============================

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
        if (matchCount == null) {
            matchCount = 0;
        }
    }

    // ==============================
    // Getters and Setters
    // ==============================

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public String getQueryText() {
        return queryText;
    }

    public void setQueryText(String queryText) {
        this.queryText = queryText;
    }

    public SearchQueryType getQueryType() {
        return queryType;
    }

    public void setQueryType(SearchQueryType queryType) {
        this.queryType = queryType;
    }

    public Integer getMatchCount() {
        return matchCount;
    }

    public void setMatchCount(Integer matchCount) {
        this.matchCount = matchCount;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}