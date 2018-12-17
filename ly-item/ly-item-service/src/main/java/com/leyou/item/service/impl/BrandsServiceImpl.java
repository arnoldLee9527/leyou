package com.leyou.item.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.leyou.item.mapper.BrandsMapper;
import com.leyou.item.pojo.Brand;
import com.leyou.item.service.BrandsService;
import com.leyou.pojo.PageResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandsServiceImpl implements BrandsService {

    @Autowired
    private BrandsMapper brandsMapper;

    @Override
    public PageResult<Brand> queryBrandByPageAndSort(Integer page, Integer rowsPerPage, String sortBy, Boolean desc, String key) {

        //开启分页
        PageHelper.startPage(page,rowsPerPage);

        //添加判断条件
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();

        //判断key是否为空
        if (StringUtils.isNotBlank(key)){
            criteria.andEqualTo("letter",key.toUpperCase());
        }
        //判断sortBy是否为空
        if (StringUtils.isNotBlank(sortBy)){
            String orderByClause = sortBy + (desc ? " DESC":" ASC");
            example.setOrderByClause(orderByClause);
        }


        //将查询结果封装到page中
        Page<Brand> brandPage = (Page<Brand>) brandsMapper.selectByExample(example);

        PageResult<Brand> pageResult = new PageResult<>(brandPage.getTotal(),new Long(brandPage.getPages()),brandPage.getResult());

        return pageResult;
    }

    @Override
    @Transactional //添加事务管理
    public void saveBrand(Brand brand, List<Long> cids) {
        //新增品牌
        brandsMapper.insertSelective(brand);
        //维护中间表，新增品牌和分类中间表
        for (Long cid : cids) {
            brandsMapper.insertCategoryBrand(cid, brand.getId());
        };

    }

    @Override
    public void editBrand(Brand brand, List<Long> cids) {
        //修改
        brandsMapper.updateByPrimaryKeySelective(brand);
    }

    @Override
    @Transactional
    public void deleteBrand(Long bid) {
        //删除
        brandsMapper.deleteBrand(bid);
    }

    @Override
    public List<Brand> queryBrandByCategory(Long cid) {
        List<Brand> brands = brandsMapper.queryBrandByCategory(cid);
        return brands;
    }

    //商品列表页面-品牌查询
    public Brand queryBrandById(Long bid) {
        Brand brand = brandsMapper.selectByPrimaryKey(bid);
        return brand;
    }
}
