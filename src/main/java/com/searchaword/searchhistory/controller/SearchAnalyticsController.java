package com.searchaword.searchhistory.controller;

import com.searchaword.searchhistory.dto.TopQueryResponse;
import com.searchaword.searchhistory.dto.SearchTrendResponse;
import com.searchaword.searchhistory.service.SearchAnalyticsService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics")
public class SearchAnalyticsController {

    private final SearchAnalyticsService searchAnalyticsService;

    public SearchAnalyticsController(SearchAnalyticsService searchAnalyticsService) {
        this.searchAnalyticsService = searchAnalyticsService;
    }

    // ============================================
    // 🔥 Global Top Queries
    // ============================================

    @GetMapping("/top-queries")
    public ResponseEntity<List<TopQueryResponse>> getTopQueries(
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(
                searchAnalyticsService.getTopQueries(limit)
        );
    }

    // ============================================
    // 📈 Daily Search Trends
    // ============================================

    @GetMapping("/trends")
    public ResponseEntity<List<SearchTrendResponse>> getDailySearchTrends(
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to
    ) {
        return ResponseEntity.ok(
                searchAnalyticsService.getDailySearchTrends(from, to)
        );
    }
}