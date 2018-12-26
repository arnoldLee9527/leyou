package com.leyou.service.impl;

import com.leyou.client.BrandClient;
import com.leyou.client.CategoryClient;
import com.leyou.client.SpecClient;
import com.leyou.item.pojo.*;
import com.leyou.client.GoodsClient;
import com.leyou.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private SpecClient specClient;

    @Autowired
    private CategoryClient categoryClient;

    @Override
    public Map<String, Object> loadModel(Long id) {
        try {
            //查询spu
            Spu spu = goodsClient.querySpuById(id);

            //查询spuDetail
            SpuDetail spuDetail = goodsClient.querySpuDetailById(id);

            //查询sku
            List<Sku> skus = goodsClient.querySkuById(id);

            //查询brand
            List<Brand> brands = brandClient.queryBrandByIds(Collections.singletonList(spu.getBrandId()));

            //查询groups
            List<SpecGroup> groups = specClient.querySpecGroups(spu.getCid3());

            //查询params TODO 处理成map id:name格式的键值对
            List<SpecParam> specParams = specClient.querySpecParams(null, spu.getCid3(), false, null);
            Map<Long, String> paramMap = new HashMap<>();
            specParams.forEach(specParam -> {
                paramMap.put(specParam.getId(), specParam.getName());
            });


            //查询categories
            List<String> names = categoryClient.queryNamesById(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            List<Category> categoryList = new ArrayList<>();
            Category c1 = new Category();
            c1.setName(names.get(0));
            c1.setId(spu.getCid1());
            categoryList.add(c1);

            Category c2 = new Category();
            c2.setName(names.get(1));
            c2.setId(spu.getCid2());
            categoryList.add(c2);

            Category c3 = new Category();
            c3.setName(names.get(2));
            c3.setId(spu.getCid3());
            categoryList.add(c3);


            Map<String, Object> modelMap = new HashMap<>();
            modelMap.put("spu", spu);
            modelMap.put("spuDetail", spuDetail);
            modelMap.put("skus", skus);
            modelMap.put("brand", brands.get(0));
            modelMap.put("groups", groups);
            modelMap.put("paramMap", paramMap);
            modelMap.put("categories", categoryList);
            return modelMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
