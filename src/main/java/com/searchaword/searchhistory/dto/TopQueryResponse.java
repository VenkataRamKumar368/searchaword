package com.searchaword.searchhistory.dto;

public class TopQueryResponse {

    private String query;
    private long count;

    public TopQueryResponse(String query, long count) {
        this.query = query;
        this.count = count;
    }

    public String getQuery() {
        return query;
    }

    public long getCount() {
        return count;
    }
}