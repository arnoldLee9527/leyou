package com.leyou.search.service.impl;

import com.leyou.item.pojo.SpuBo;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.PageResult;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.repository.GoodsRepository;
import com.leyou.search.service.SerarchService;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.search.MatchQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

@Service
public class SerarchServiceImpl implements SerarchService {

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private GoodsRepository goodsRepository;

    @Override
    public PageResult<Goods> search(SearchRequest searchRequest) {
        String key = searchRequest.getKey();

        //如果用户没有输入搜索信息，返回默认
        if (StringUtils.isBlank(key)){
            return null;

        }
        //获取分页信息
        Integer page = searchRequest.getPage()-1;
        Integer pageSize = searchRequest.getSize();

        //创建查询构造器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        //对结果进行筛选
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","skus","subTitle"}, null));
        //全文搜索
        queryBuilder.withQuery(QueryBuilders.matchQuery("all", key));
        //分页
        queryBuilder.withPageable(PageRequest.of(page,pageSize));
        Page<Goods> goodsPage = goodsRepository.search(queryBuilder.build());

        PageResult<Goods> pageResult = new PageResult<>(goodsPage.getTotalElements(), new Long(goodsPage.getTotalPages()),goodsPage.getContent());

        return pageResult;

    }
}
