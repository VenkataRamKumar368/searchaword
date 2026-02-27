package com.searchaword.searchhistory.dto;

import java.time.LocalDate;

public class SearchTrendResponse {

    private LocalDate date;
    private Long count;

    public SearchTrendResponse(LocalDate date, Long count) {
        this.date = date;
        this.count = count;
    }

    public LocalDate getDate() {
        return date;
    }

    public Long getCount() {
        return count;
    }
}