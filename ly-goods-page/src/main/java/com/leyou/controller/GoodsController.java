package com.leyou.controller;

import com.leyou.item.pojo.Spu;
import com.leyou.service.GoodsService;
import com.leyou.service.impl.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("item")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private FileService fileService;


    @GetMapping("{id}.html")
    public String loadModel(@PathVariable("id")Long id, Model model){

        Map<String, Object> map = goodsService.loadModel(id);

        model.addAllAttributes(map);

        // 判断是否需要生成新的页面
        if(!this.fileService.exists(id)){
            this.fileService.syncCreateHtml(id);
        }

        return "item";
    }

}
