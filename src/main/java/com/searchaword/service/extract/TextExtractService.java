package com.searchaword.service.extract;

import org.springframework.web.multipart.MultipartFile;

public interface TextExtractService {

    String extractText(MultipartFile file);
}