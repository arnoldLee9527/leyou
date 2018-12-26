package com.leyou.search.controller;

import com.leyou.search.service.SerarchService;
import com.leyou.search.utils.SearchRequest;
import com.leyou.search.utils.SearchResult;
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
    public ResponseEntity<SearchResult> search(@RequestBody SearchRequest searchRequest){

        SearchResult searchResult = serarchService.search(searchRequest);

        if (searchResult != null) {
            return ResponseEntity.ok(searchResult);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
