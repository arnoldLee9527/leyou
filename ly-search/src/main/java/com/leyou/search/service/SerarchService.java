package com.leyou.search.service;

import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.PageResult;
import com.leyou.search.pojo.SearchRequest;

public interface SerarchService {
    PageResult<Goods> search(SearchRequest searchRequest);
}
