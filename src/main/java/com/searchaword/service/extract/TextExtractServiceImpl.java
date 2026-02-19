package com.searchaword.service.extract;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class TextExtractServiceImpl implements TextExtractService {

    @Override
    public String extractText(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        String filename = file.getOriginalFilename();

        if (filename == null) {
            throw new RuntimeException("Invalid file");
        }

        try {
            if (filename.endsWith(".pdf")) {
                return extractPdf(file);
            } else if (filename.endsWith(".docx")) {
                return extractDocx(file);
            } else if (filename.endsWith(".txt")) {
                return extractTxt(file);
            } else {
                throw new RuntimeException("Unsupported file type. Only PDF, DOCX, TXT allowed.");
            }

        } catch (IOException e) {
            throw new RuntimeException("Error processing file", e);
        }
    }

    private String extractPdf(MultipartFile file) throws IOException {
        PDDocument document = PDDocument.load(file.getInputStream());
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(document);
        document.close();
        return text;
    }

    private String extractDocx(MultipartFile file) throws IOException {
        XWPFDocument doc = new XWPFDocument(file.getInputStream());
        XWPFWordExtractor extractor = new XWPFWordExtractor(doc);
        String text = extractor.getText();
        doc.close();
        return text;
    }

    private String extractTxt(MultipartFile file) throws IOException {
        return new String(file.getBytes(), StandardCharsets.UTF_8);
    }
}