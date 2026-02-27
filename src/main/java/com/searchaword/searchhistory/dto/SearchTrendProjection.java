package com.searchaword.searchhistory.dto;

import java.time.LocalDate;

public interface SearchTrendProjection {

    LocalDate getSearchDate();
    Long getTotalCount();
}