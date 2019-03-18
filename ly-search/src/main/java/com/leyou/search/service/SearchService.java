package com.leyou.search.service;

import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.pojo.SearchResult;

public interface SearchService {
    public SearchResult search(SearchRequest searchRequest);
}
