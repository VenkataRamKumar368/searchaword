package com.searchaword.documents.dto;

public record DocumentUploadResponse(
        Long documentId,
        String fileName,
        String sha256,
        boolean cached,
        String text
) {}