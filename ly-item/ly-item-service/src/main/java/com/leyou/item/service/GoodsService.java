package com.leyou.item.service;

import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.SpuBo;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.pojo.PageResult;

import java.util.List;

public interface GoodsService {
    PageResult<SpuBo> querySpuByPageAndSort(String key, Boolean saleable, Integer page, Integer rows);

    void saveGoods(SpuBo spuBo);

    void updateGoods(SpuBo spuBo);

    SpuDetail querySpuDetailById(Long spuId);

    List<Sku> querySkuById(Long id);
}
