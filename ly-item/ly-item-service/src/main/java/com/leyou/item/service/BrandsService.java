package com.leyou.item.service;

import com.leyou.item.pojo.Brand;
import com.leyou.search.pojo.PageResult;

import java.util.List;

public interface BrandsService {
    PageResult<Brand> queryBrandByPageAndSort(Integer page, Integer rowsPerPage, String sortBy, Boolean desc, String key);

    void saveBrand(Brand brand, List<Long> cids);

    void editBrand(Brand brand, List<Long> cids);

    void deleteBrand(Long bid);

    List<Brand> queryBrandByCategory(Long cid);
}
