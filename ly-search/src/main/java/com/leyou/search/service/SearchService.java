package com.leyou.search.service;

import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.pojo.SearchResult;

import java.io.IOException;

public interface SearchService {
    public SearchResult search(SearchRequest searchRequest) throws Exception;

    public void createIndex(Long id) throws IOException;

    public void deleteIndex(Long id) throws IOException;
}
