package com.leyou.search.utils;

import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.PageResult;

import java.util.List;
import java.util.Map;

public class SearchResult extends PageResult {
    private List<Category> categories;

    private List<Brand> brands;

    private List<Map<String,Object>> specs; // 规格参数过滤条件

    public SearchResult(Long total, Long totalPage, List<Goods> items,
                        List<Category> categories, List<Brand> brands,
                        List<Map<String,Object>> specs) {
        super(total, totalPage, items);
        this.categories = categories;
        this.brands = brands;
        this.specs = specs;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<Brand> getBrands() {
        return brands;
    }

    public void setBrands(List<Brand> brands) {
        this.brands = brands;
    }

    public List<Map<String, Object>> getSpecs() {
        return specs;
    }

    public void setSpecs(List<Map<String, Object>> specs) {
        this.specs = specs;
    }
}
