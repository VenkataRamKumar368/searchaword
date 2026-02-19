package com.searchaword.documents.dto;

import java.time.LocalDateTime;

public record DocumentListResponse(
        Long id,
        String fileName,
        Long fileSize,
        LocalDateTime uploadedAt
) {}