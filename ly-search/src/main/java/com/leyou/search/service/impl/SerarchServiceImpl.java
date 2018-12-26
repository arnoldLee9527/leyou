package com.leyou.search.service.impl;

import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import com.leyou.item.pojo.SpecParam;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.PageResult;
import com.leyou.search.utils.SearchRequest;
import com.leyou.search.repository.GoodsRepository;
import com.leyou.search.service.SerarchService;
import com.leyou.search.utils.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SerarchServiceImpl implements SerarchService {

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private SpecClient specClient;

    @Override
    public SearchResult search(SearchRequest searchRequest) {
        String key = searchRequest.getKey();

        //如果用户没有输入搜索信息，返回默认
        if (StringUtils.isBlank(key)) {
            return null;

        }
        //获取分页信息
        Integer page = searchRequest.getPage() - 1;
        Integer pageSize = searchRequest.getSize();

        //创建查询构造器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        //对结果进行筛选
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "skus", "subTitle"}, null));
        //全文搜索
        queryBuilder.withQuery(QueryBuilders.matchQuery("all", key));

        //聚合查询
        //商品聚合名称
        String categoryAggName = "category"; // 商品分类聚合名称
        String brandAggName = "brand"; // 品牌聚合名称
        //聚合查询条件
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));
        //查询
        AggregatedPage<Goods> aggregatedPage = (AggregatedPage<Goods>) goodsRepository.search(queryBuilder.build());
        //获取聚合
        LongTerms category = (LongTerms) aggregatedPage.getAggregation(categoryAggName);
        LongTerms brand = (LongTerms) aggregatedPage.getAggregation(brandAggName);
        //获取桶
        List<LongTerms.Bucket> categoryBuckets = category.getBuckets();
        List<Long> categoryIds = new ArrayList<>();
        for (LongTerms.Bucket categoryBucket : categoryBuckets) {
            categoryIds.add(categoryBucket.getKeyAsNumber().longValue());
        }

        List<String> names = categoryClient.queryNamesById(categoryIds);
        List<Category> categories = new ArrayList<>();
        for (int i = 0; i < categoryIds.size(); i++) {
            Category categorys = new Category();
            categorys.setId(categoryIds.get(i));
            categorys.setName(names.get(i));

            categories.add(categorys);
        }


        List<Long> brandIds = new ArrayList<>();
        List<LongTerms.Bucket> brandBuckets = brand.getBuckets();
        for (LongTerms.Bucket brandBucket : brandBuckets) {
            brandIds.add(brandBucket.getKeyAsNumber().longValue());
        }

        //查询brands
        List<Brand> brands = brandClient.queryBrandByIds(brandIds);

        //分页
        queryBuilder.withPageable(PageRequest.of(page, pageSize));
        Page<Goods> goodsPage = goodsRepository.search(queryBuilder.build());

        PageResult<Goods> pageResult = new PageResult<>(goodsPage.getTotalElements(), new Long(goodsPage.getTotalPages()), goodsPage.getContent());

        //规格参数聚合查询
        List<Map<String, Object>> specs = null;
        if (categories.size() == 1) {
            Long cid = categories.get(0).getId();

            specs = getSpecs(cid);
        }

        return new SearchResult(pageResult.getTotal(), pageResult.getTotalPage(), pageResult.getItems(), categories, brands, specs);

    }

    private List<Map<String, Object>> getSpecs(Long cid) {
        List<Map<String, Object>> specs = new ArrayList<>();

        //查询可搜索的字段
        List<SpecParam> specParams = specClient.querySpecParams(null, cid, null, true);

        //聚合查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        for (SpecParam specParam : specParams) {
            queryBuilder.addAggregation(AggregationBuilders.terms(specParam.getName()).field("specs." + specParam.getName() + ".keyword"));
        }
        AggregatedPage aggregatedPage = (AggregatedPage) goodsRepository.search(queryBuilder.build());

        for (SpecParam specParam : specParams) {
            Map<String, Object> spec = new HashMap<>();

            StringTerms stringTerms = (StringTerms) aggregatedPage.getAggregation(specParam.getName());
            spec.put("k", specParam.getName());

            List<StringTerms.Bucket> buckets = stringTerms.getBuckets();

            List<Object> options = new ArrayList<>();
            for (StringTerms.Bucket bucket : buckets) {
                options.add(bucket.getKeyAsString());
            }

            spec.put("options", options);

            specs.add(spec);


        }

        return specs;
    }


}
