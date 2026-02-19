package com.searchaword.dto;

public class SearchResponse {

    private String word;
    private int count;
    private String highlightedHtml;

    public SearchResponse() {}

    public SearchResponse(String word, int count, String highlightedHtml) {
        this.word = word;
        this.count = count;
        this.highlightedHtml = highlightedHtml;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getHighlightedHtml() {
        return highlightedHtml;
    }

    public void setHighlightedHtml(String highlightedHtml) {
        this.highlightedHtml = highlightedHtml;
    }
}