package com.leyou.item.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.leyou.item.mapper.GoodsMapper;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuBo;
import com.leyou.item.service.GoodsService;
import com.leyou.pojo.PageResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private CategoryServiceImpl categoryService;

    @Autowired
    private BrandsServiceImpl brandsService;

    @Override
    public PageResult<SpuBo> querySpuByPageAndSort(String key, Boolean saleable, Integer page, Integer rows) {
        //开启分页
        PageHelper.startPage(page,rows);

        //添加判断条件
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();

        //判断是否有过滤
        if (StringUtils.isNotBlank(key)){
            criteria.andLike("title","%"+key+"%");
        }
        //判断是否上下架
        if (saleable != null){
            criteria.andEqualTo("saleable",saleable);
        }
        // TODO
        Page<Spu> spuPage = (Page<Spu>) goodsMapper.selectByExample(example);

        List<SpuBo> spuBoList = new ArrayList<>();
        List<Spu> spuList = new ArrayList<>();

        if (null != spuPage){
            spuList = spuPage.getResult();
        }

        if (null != spuList){
            spuList.forEach(spu -> {
                SpuBo spuBo = new SpuBo();
                //将spu赋值到spuBo
                BeanUtils.copyProperties(spu,spuBo);

                //查询cname
                List<String> names = this.categoryService.queryNamesById(
                        Arrays.asList(spu.getCid1(),spu.getCid2(),spu.getCid3())
                );
                spuBo.setCname(StringUtils.join(names,"/"));
                //查询bname
                Brand brand = this.brandsService.queryBrandById(spu.getBrandId());
                spuBo.setBname(brand.getName());

                spuBoList.add(spuBo);

            });
        }

        return new PageResult<>(spuPage.getTotal(),new Long(spuPage.getPages()),spuBoList);
    }
}
