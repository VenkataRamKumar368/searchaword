package com.searchaword.controller;

import com.searchaword.documents.dto.DocumentUploadResponse;
import com.searchaword.documents.dto.DocumentListResponse;
import com.searchaword.documents.service.DocumentService;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/documents")
@CrossOrigin
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
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
}