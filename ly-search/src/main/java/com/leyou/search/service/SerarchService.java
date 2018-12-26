package com.leyou.search.service;

import com.leyou.search.utils.SearchRequest;
import com.leyou.search.utils.SearchResult;

public interface SerarchService {
    SearchResult search(SearchRequest searchRequest);
}
