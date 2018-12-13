package com.leyou.item.controller;

import com.leyou.item.pojo.Brand;
import com.leyou.item.service.BrandsService;
import com.leyou.pojo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("brand")
public class BrandsController {
    @Autowired
    private BrandsService brandsService;


    @RequestMapping("page")
    public ResponseEntity<PageResult<Brand>> queryBrandByPageAndSort(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rowsPerPage", defaultValue = "5") Integer rowsPerPage,
            @RequestParam(value = "sortBy",required = false) String sortBy,
            @RequestParam(value = "desc", defaultValue = "false")Boolean desc,
            @RequestParam(value = "key",required = false) String key
    ){
        PageResult<Brand> brands = brandsService.queryBrandByPageAndSort(page, rowsPerPage, sortBy, desc, key);
        if (brands != null && 0!=brands.getItems().size()) {
            return ResponseEntity.ok(brands);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();


    }

    @PostMapping
    public void saveBrand(Brand brand, @RequestParam("cids")List<Long> cids){
        brandsService.saveBrand(brand, cids);
    }

    @PutMapping
    public void editBrand(Brand brand, @RequestParam("cids")List<Long> cids){
        brandsService.editBrand(brand, cids);
    }

    @DeleteMapping("delete/{bid}")
    public void deleteBrand(@PathVariable("bid")Long bid){
        brandsService.deleteBrand(bid);
    }

}
