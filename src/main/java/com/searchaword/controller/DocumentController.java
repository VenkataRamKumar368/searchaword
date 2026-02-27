package com.searchaword.controller;

import com.searchaword.documents.dto.DocumentUploadResponse;
import com.searchaword.documents.dto.DocumentListResponse;
import com.searchaword.documents.dto.FullTextSearchResponse;
import com.searchaword.documents.service.DocumentService;

import com.searchaword.searchhistory.SearchHistoryEntity;
import com.searchaword.searchhistory.SearchHistoryRepository;
import com.searchaword.searchhistory.dto.TopQueryResponse;
import com.searchaword.searchhistory.service.SearchAnalyticsService;

import com.searchaword.security.entity.User;
import com.searchaword.security.repository.UserRepository;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@RestController
@RequestMapping("/api/v1/documents")
@CrossOrigin
public class DocumentController {

    private final DocumentService documentService;
    private final SearchHistoryRepository searchHistoryRepository;
    private final UserRepository userRepository;
    private final SearchAnalyticsService searchAnalyticsService;

    public DocumentController(
            DocumentService documentService,
            SearchHistoryRepository searchHistoryRepository,
            UserRepository userRepository,
            SearchAnalyticsService searchAnalyticsService
    ) {
        this.documentService = documentService;
        this.searchHistoryRepository = searchHistoryRepository;
        this.userRepository = userRepository;
        this.searchAnalyticsService = searchAnalyticsService;
    }

    // ============================================
    // Upload Endpoint
    // ============================================

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<DocumentUploadResponse> upload(
            @RequestParam("file") MultipartFile file
    ) {

        DocumentUploadResponse response =
                documentService.uploadAndExtract(file);

        return ResponseEntity.ok(response);
    }

    // ============================================
    // List Documents
    // ============================================

    @GetMapping
    public ResponseEntity<List<DocumentListResponse>> listDocuments() {

        List<DocumentListResponse> documents =
                documentService.listDocuments();

        return ResponseEntity.ok(documents);
    }

    // ============================================
    // Get Document By ID
    // ============================================

    @GetMapping("/{id}")
    public ResponseEntity<DocumentUploadResponse> getDocumentById(
            @PathVariable Long id
    ) {

        DocumentUploadResponse document =
                documentService.getDocumentById(id);

        return ResponseEntity.ok(document);
    }

    // ============================================
    // Letter-Based Word Search
    // ============================================

    @GetMapping("/{id}/letter-search")
    public ResponseEntity<List<String>> searchWordsByLetters(
            @PathVariable Long id,
            @RequestParam String letters
    ) {

        List<String> matchingWords =
                documentService.searchWordsByLetters(id, letters);

        return ResponseEntity.ok(matchingWords);
    }

    // ============================================
    // Download Letter Search Result (TXT / PDF)
    // ============================================

    @GetMapping("/{id}/letter-search/download")
    public ResponseEntity<byte[]> downloadLetterSearch(
            @PathVariable Long id,
            @RequestParam String letters,
            @RequestParam String type
    ) {

        byte[] fileBytes =
                documentService.generateLetterSearchFile(id, letters, type);

        String fileName = "letter-search-result." + type.toLowerCase();

        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=" + fileName)
                .body(fileBytes);
    }

    // ============================================
    // FULL TEXT SEARCH
    // ============================================

    @GetMapping("/search/fulltext")
    public ResponseEntity<Page<FullTextSearchResponse>> fullTextSearch(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {

        Page<FullTextSearchResponse> results =
                documentService.fullTextSearch(query, page, size);

        return ResponseEntity.ok(results);
    }

    // ============================================
    // Search History (Paginated)
    // ============================================

    @GetMapping("/search-history")
    public ResponseEntity<Page<SearchHistoryEntity>> getSearchHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow();

        Pageable pageable = PageRequest.of(page, size);

        Page<SearchHistoryEntity> history =
                searchHistoryRepository
                        .findByUserIdOrderByCreatedAtDesc(
                                user.getId(),
                                pageable
                        );

        return ResponseEntity.ok(history);
    }

    // ============================================
    // 🔥 Analytics: Top Queries
    // ============================================

    @GetMapping("/analytics/top-queries")
    public ResponseEntity<List<TopQueryResponse>> getTopQueries(
            @RequestParam(defaultValue = "10") int limit
    ) {

        List<TopQueryResponse> results =
                searchAnalyticsService.getTopQueries(limit);

        return ResponseEntity.ok(results);
    }
}