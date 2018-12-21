package com.leyou.item.api;

import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.SpuBo;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.search.pojo.PageResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface GoodsApi {

    @GetMapping("sku/list")
    List<Sku> querySkuById(@RequestParam("id")Long id);

    @GetMapping("spu/page")
    PageResult<SpuBo> querySpuByPageAndSort(
        @RequestParam(value = "key", required = false) String key,
        @RequestParam(value = "saleable", required = false)Boolean saleable,
        @RequestParam(value = "page", defaultValue = "1")Integer page,
        @RequestParam(value = "rows",defaultValue = "5")Integer rows
    );

    @GetMapping("spu/detail/{spuId}")
    SpuDetail querySpuDetailById(@PathVariable("spuId")Long spuId);


}
