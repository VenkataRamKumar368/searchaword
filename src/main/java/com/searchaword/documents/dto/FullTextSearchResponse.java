package com.searchaword.documents.dto;

public class FullTextSearchResponse {

    private Long documentId;
    private String fileName;
    private Double rank;
    private String snippet;

    public FullTextSearchResponse(Long documentId,
                                  String fileName,
                                  Double rank,
                                  String snippet) {
        this.documentId = documentId;
        this.fileName = fileName;
        this.rank = rank;
        this.snippet = snippet;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public String getFileName() {
        return fileName;
    }

    public Double getRank() {
        return rank;
    }

    public String getSnippet() {
        return snippet;
    }
}