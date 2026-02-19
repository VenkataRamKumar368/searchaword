package com.searchaword.service.search;

import com.searchaword.dto.SearchResponse;

public interface WordSearchService {

    SearchResponse searchAndHighlight(String content, String word);
}