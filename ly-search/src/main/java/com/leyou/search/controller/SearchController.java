package com.leyou.search.controller;

import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.PageResult;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.service.SerarchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController {

    @Autowired
    private SerarchService serarchService;

    @PostMapping("page")
    public ResponseEntity<PageResult<Goods>> search(@RequestBody SearchRequest searchRequest){

        PageResult<Goods> pageResult = serarchService.search(searchRequest);

        if (pageResult != null && 0!=pageResult.getItems().size()) {
            return ResponseEntity.ok(pageResult);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
