package com.leyou.item.service.impl;

import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Category;
import com.leyou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public List<Category> queryByParentId(Long pid) {
        Category category = new Category();
        category.setParentId(pid);
        List select = categoryMapper.select(category);
        return select;
    }

    @Override
    public List<Category> queryByBrandId(Long bid) {
        List categories = categoryMapper.queryByBrandId(bid);
        return categories;
    }

    // 根据商品分类id查询名称
    public List<String> queryNamesById(List<Long> idList) {
        List<Category> categories = categoryMapper.selectByIdList(idList);
        //将品牌名称转换为字符串数组返回
        List<String> names = new ArrayList<>();
        for (Category category : categories) {
            names.add(category.getName());
        }
        return names;

    }
}
