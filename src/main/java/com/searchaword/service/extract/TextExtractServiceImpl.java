package com.searchaword.service.extract;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.io.MemoryUsageSetting;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
public class TextExtractServiceImpl implements TextExtractService {

    @Override
    public String extractText(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "File is empty"
            );
        }

        String filename = file.getOriginalFilename();

        if (filename == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid file"
            );
        }

        String lowerFileName = filename.toLowerCase(Locale.ROOT);

        try {

            if (lowerFileName.endsWith(".pdf")) {
                return extractPdf(file);
            }

            if (lowerFileName.endsWith(".docx")) {
                return extractDocx(file);
            }

            if (lowerFileName.endsWith(".txt")) {
                return extractTxt(file);
            }

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Unsupported file type. Only PDF, DOCX, TXT allowed."
            );

        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Failed to process file. It may be corrupted or too large."
            );
        }
    }

    // ===============================
    // MEMORY SAFE PDF EXTRACTION
    // ===============================

    private String extractPdf(MultipartFile file) throws IOException {

        try (PDDocument document = PDDocument.load(
                file.getInputStream(),
                MemoryUsageSetting.setupTempFileOnly()  // 🔥 CLOUD SAFE
        )) {

            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    // ===============================
    // DOCX EXTRACTION
    // ===============================

    private String extractDocx(MultipartFile file) throws IOException {

        try (XWPFDocument doc = new XWPFDocument(file.getInputStream());
             XWPFWordExtractor extractor = new XWPFWordExtractor(doc)) {

            return extractor.getText();
        }
    }

    // ===============================
    // TXT EXTRACTION
    // ===============================

    private String extractTxt(MultipartFile file) throws IOException {

        StringBuilder text = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                text.append(line).append(System.lineSeparator());
            }
        }

        return text.toString();
    }
}