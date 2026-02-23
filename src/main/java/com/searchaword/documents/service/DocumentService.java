package com.searchaword.documents.service;

import com.searchaword.documents.domain.DocumentEntity;
import com.searchaword.documents.dto.DocumentUploadResponse;
import com.searchaword.documents.dto.DocumentListResponse;
import com.searchaword.documents.repository.DocumentRepository;
import com.searchaword.documents.util.HashUtil;
import com.searchaword.service.extract.TextExtractService;
import com.searchaword.security.entity.User;
import com.searchaword.security.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final TextExtractService textExtractService;
    private final UserRepository userRepository;

    public DocumentService(DocumentRepository documentRepository,
                           TextExtractService textExtractService,
                           UserRepository userRepository) {
        this.documentRepository = documentRepository;
        this.textExtractService = textExtractService;
        this.userRepository = userRepository;
    }

    // ============================================
    // Helper: Get Logged-in User
    // ============================================

    private User getCurrentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "User not authenticated"
            );
        }

        String username = authentication.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found"
                        ));
    }

    // ============================================
    // Upload + Assign Owner
    // ============================================

    public DocumentUploadResponse uploadAndExtract(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "File cannot be empty"
            );
        }

        User currentUser = getCurrentUser();

        String sha256;

        try (InputStream is = file.getInputStream()) {
            sha256 = HashUtil.sha256Hex(is);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unable to compute file hash"
            );
        }

        var existingDoc = documentRepository.findBySha256AndOwner(sha256, currentUser);

        if (existingDoc.isPresent()) {
            var doc = existingDoc.get();
            return new DocumentUploadResponse(
                    doc.getId(),
                    doc.getOriginalFileName(),
                    doc.getSha256(),
                    true,
                    doc.getExtractedText()
            );
        }

        String extractedText = textExtractService.extractText(file);

        DocumentEntity doc = new DocumentEntity();
        doc.setOriginalFileName(file.getOriginalFilename());
        doc.setContentType(file.getContentType());
        doc.setFileSize(file.getSize());
        doc.setSha256(sha256);
        doc.setExtractedText(extractedText);
        doc.setOwner(currentUser); // 🔥 OWNER ASSIGNED

        DocumentEntity saved = documentRepository.save(doc);

        return new DocumentUploadResponse(
                saved.getId(),
                saved.getOriginalFileName(),
                saved.getSha256(),
                false,
                saved.getExtractedText()
        );
    }

    // ============================================
    // List Documents (OWNER ONLY)
    // ============================================

    public List<DocumentListResponse> listDocuments() {

        User currentUser = getCurrentUser();

        return documentRepository
                .findAllByOwnerOrderByCreatedAtDesc(currentUser)
                .stream()
                .map(doc -> new DocumentListResponse(
                        doc.getId(),
                        doc.getOriginalFileName(),
                        doc.getFileSize(),
                        doc.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    // ============================================
    // Get Document By ID (OWNER CHECK)
    // ============================================

    public DocumentUploadResponse getDocumentById(Long id) {

        User currentUser = getCurrentUser();

        DocumentEntity doc = documentRepository
                .findByIdAndOwner(id, currentUser)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Document not found"
                        ));

        return new DocumentUploadResponse(
                doc.getId(),
                doc.getOriginalFileName(),
                doc.getSha256(),
                true,
                doc.getExtractedText()
        );
    }

    // ============================================
    // Letter Search (OWNER CHECK)
    // ============================================

    public List<String> searchWordsByLetters(Long id, String letters) {

        if (letters == null || letters.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Letters parameter cannot be empty"
            );
        }

        User currentUser = getCurrentUser();

        DocumentEntity doc = documentRepository
                .findByIdAndOwner(id, currentUser)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Document not found"
                        ));

        String content = doc.getExtractedText();

        if (content == null || content.isBlank()) {
            return List.of();
        }

        List<String> requiredLetters = Arrays.stream(letters.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        return Arrays.stream(content.toLowerCase().split("\\W+"))
                .filter(word ->
                        requiredLetters.stream()
                                .allMatch(word::contains)
                )
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    // ============================================
    // Generate File (OWNER SAFE)
    // ============================================

    public byte[] generateLetterSearchFile(Long id,
                                           String letters,
                                           String type) {

        List<String> words = searchWordsByLetters(id, letters);

        if (words.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No matching words found"
            );
        }

        if (type == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "File type must be provided (txt or pdf)"
            );
        }

        type = type.toLowerCase();

        switch (type) {

            case "txt":
                String content = String.join(System.lineSeparator(), words);
                return content.getBytes(StandardCharsets.UTF_8);

            case "pdf":
                try (PDDocument document = new PDDocument();
                     ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

                    PDPage page = new PDPage();
                    document.addPage(page);

                    PDPageContentStream contentStream =
                            new PDPageContentStream(document, page);

                    contentStream.setFont(PDType1Font.HELVETICA, 12);
                    contentStream.beginText();
                    contentStream.setLeading(14.5f);
                    contentStream.newLineAtOffset(50, 750);

                    for (String word : words) {
                        contentStream.showText(word);
                        contentStream.newLine();
                    }

                    contentStream.endText();
                    contentStream.close();

                    document.save(baos);
                    return baos.toByteArray();

                } catch (Exception e) {
                    throw new ResponseStatusException(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "Failed to generate PDF"
                    );
                }

            default:
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Unsupported file type. Use 'txt' or 'pdf'"
                );
        }
    }
}