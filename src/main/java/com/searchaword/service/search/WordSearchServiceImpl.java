package com.searchaword.service.search;

import com.searchaword.dto.SearchResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

@Service
public class WordSearchServiceImpl implements WordSearchService {

    @Override
    public SearchResponse searchAndHighlight(String content, String word) {

        // Escape HTML for safety
        String safeContent = HtmlUtils.htmlEscape(content);

        // Backend now only extracts and returns plain content
        // No search or highlighting logic here
        return new SearchResponse("", 0, safeContent);
    }
}