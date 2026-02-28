package com.searchaword.searchhistory.service;

import com.searchaword.searchhistory.SearchHistoryRepository;
import com.searchaword.searchhistory.dto.TopQueryResponse;
import com.searchaword.searchhistory.dto.SearchTrendProjection;
import com.searchaword.searchhistory.dto.SearchTrendResponse;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class SearchAnalyticsService {

    private final SearchHistoryRepository searchHistoryRepository;

    public SearchAnalyticsService(SearchHistoryRepository searchHistoryRepository) {
        this.searchHistoryRepository = searchHistoryRepository;
    }

    // ============================================
    // 🔥 Get Top Global Queries
    // ============================================

    public List<TopQueryResponse> getTopQueries(int limit) {

        if (limit <= 0) {
            limit = 10;
        }

        return searchHistoryRepository.findTopQueries(
                PageRequest.of(0, limit)
        );
    }

    // ============================================
    // 📈 Daily Search Trends (Optional Date Range)
    // ============================================

    public List<SearchTrendResponse> getDailySearchTrends(
            LocalDate from,
            LocalDate to
    ) {

        List<SearchTrendProjection> results;

        if (from != null && to != null) {

            // ✅ Use system timezone (matches your DB: America/Chicago)
            ZoneId zone = ZoneId.systemDefault();

            OffsetDateTime start = from
                    .atStartOfDay()
                    .atZone(zone)
                    .toOffsetDateTime();

            OffsetDateTime end = to
                    .atTime(23, 59, 59, 999_999_999)
                    .atZone(zone)
                    .toOffsetDateTime();

            results = searchHistoryRepository.getDailySearchTrendsBetween(
                    start,
                    end
            );

        } else {

            results = searchHistoryRepository.getDailySearchTrends();
        }

        return results.stream()
                .map(r -> new SearchTrendResponse(
                        r.getSearchDate(),
                        r.getTotalCount()
                ))
                .toList();
    }
}