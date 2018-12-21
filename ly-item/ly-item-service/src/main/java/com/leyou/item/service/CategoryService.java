package com.leyou.item.service;

import com.leyou.item.pojo.Category;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CategoryService {
    List<Category> queryByParentId(@Param("pid") Long pid);

    List<Category> queryByBrandId(@Param("bid") Long bid);

    List<String> queryNamesById(@Param("idList") List<Long> idList);
}
