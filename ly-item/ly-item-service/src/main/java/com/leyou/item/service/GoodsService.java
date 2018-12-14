package com.leyou.item.service;

import com.leyou.item.pojo.SpuBo;
import com.leyou.pojo.PageResult;

public interface GoodsService {
    PageResult<SpuBo> querySpuByPageAndSort(String key, Boolean saleable, Integer page, Integer rows);
}
