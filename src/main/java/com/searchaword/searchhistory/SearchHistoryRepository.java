package com.searchaword.searchhistory;

import com.searchaword.searchhistory.dto.TopQueryResponse;
import com.searchaword.searchhistory.dto.SearchTrendProjection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistoryEntity, Long> {

    // ============================================
    // Get paginated search history for a user
    // ============================================

    Page<SearchHistoryEntity> findByUserIdOrderByCreatedAtDesc(
            Long userId,
            Pageable pageable
    );

    // ============================================
    // Get search history by user + query type
    // ============================================

    Page<SearchHistoryEntity> findByUserIdAndQueryTypeOrderByCreatedAtDesc(
            Long userId,
            SearchQueryType queryType,
            Pageable pageable
    );

    // ============================================
    // Get search history for a specific document
    // ============================================

    Page<SearchHistoryEntity> findByUserIdAndDocumentIdOrderByCreatedAtDesc(
            Long userId,
            Long documentId,
            Pageable pageable
    );

    // ============================================
    // Get recent N searches (for dashboard preview)
    // ============================================

    List<SearchHistoryEntity> findTop10ByUserIdOrderByCreatedAtDesc(
            Long userId
    );

    // ============================================
    // Count total searches by user
    // ============================================

    long countByUserId(Long userId);

    // ============================================
    // Count searches by type (WORD / LETTER / FULL_TEXT)
    // ============================================

    long countByUserIdAndQueryType(
            Long userId,
            SearchQueryType queryType
    );

    // ============================================
    // Most searched terms by specific user
    // ============================================

    @Query("""
           SELECT sh.queryText
           FROM SearchHistoryEntity sh
           WHERE sh.userId = :userId
             AND sh.queryText IS NOT NULL
           GROUP BY sh.queryText
           ORDER BY COUNT(sh.queryText) DESC
           """)
    List<String> findMostFrequentQueriesByUser(Long userId);

    // ============================================
    // 🔥 GLOBAL TOP QUERIES (Analytics)
    // ============================================

    @Query("""
        SELECT new com.searchaword.searchhistory.dto.TopQueryResponse(
            sh.queryText,
            COUNT(sh)
        )
        FROM SearchHistoryEntity sh
        WHERE sh.queryType = com.searchaword.searchhistory.SearchQueryType.FULL_TEXT
          AND sh.queryText IS NOT NULL
        GROUP BY sh.queryText
        ORDER BY COUNT(sh) DESC
    """)
    List<TopQueryResponse> findTopQueries(Pageable pageable);

    // ============================================
    // 📈 DAILY SEARCH TRENDS (ALL TIME)
    // ============================================

    @Query(value = """
        SELECT 
            DATE(created_at) AS searchDate,
            COUNT(*) AS totalCount
        FROM search_history
        WHERE query_type = 'FULL_TEXT'
        GROUP BY DATE(created_at)
        ORDER BY searchDate ASC
        """, nativeQuery = true)
    List<SearchTrendProjection> getDailySearchTrends();

    // ============================================
    // 📈 DAILY SEARCH TRENDS (DATE RANGE FILTERED)
    // ============================================

    @Query(value = """
    SELECT 
        DATE(created_at) AS searchDate,
        COUNT(*) AS totalCount
    FROM search_history
    WHERE query_type = 'FULL_TEXT'
      AND DATE(created_at) >= :fromDate
      AND DATE(created_at) <= :toDate
    GROUP BY DATE(created_at)
    ORDER BY searchDate ASC
    """, nativeQuery = true)
    List<SearchTrendProjection> getDailySearchTrendsBetween(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

}