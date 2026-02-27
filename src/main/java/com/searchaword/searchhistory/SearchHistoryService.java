package com.searchaword.searchhistory;

import org.springframework.stereotype.Service;

@Service
public class SearchHistoryService {

    private final SearchHistoryRepository repository;

    public SearchHistoryService(SearchHistoryRepository repository) {
        this.repository = repository;
    }

    public void logSearch(Long userId,
                          Long documentId,
                          String queryText,
                          SearchQueryType queryType,
                          int matchCount) {

        SearchHistoryEntity entity = new SearchHistoryEntity();
        entity.setUserId(userId);
        entity.setDocumentId(documentId);
        entity.setQueryText(queryText);
        entity.setQueryType(queryType);
        entity.setMatchCount(matchCount);

        repository.save(entity);
    }
}