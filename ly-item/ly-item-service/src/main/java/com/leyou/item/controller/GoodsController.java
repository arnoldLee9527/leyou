package com.leyou.item.controller;

import com.leyou.item.pojo.SpuBo;
import com.leyou.item.service.GoodsService;
import com.leyou.pojo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @GetMapping("spu/page")
    public ResponseEntity<PageResult<SpuBo>> querySpuByPageAndSort(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "saleable", required = false)Boolean saleable,
            @RequestParam(value = "page", defaultValue = "1")Integer page,
            @RequestParam(value = "rows",defaultValue = "5")Integer rows
            ){
        PageResult<SpuBo> spuPageResult = goodsService.querySpuByPageAndSort(key,saleable,page,rows);
        if (spuPageResult != null && 0!=spuPageResult.getItems().size()) {
            return ResponseEntity.ok(spuPageResult);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
