package com.leyou.item.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.leyou.item.mapper.GoodsDetailMapper;
import com.leyou.item.mapper.GoodsMapper;
import com.leyou.item.mapper.SkuMapper;
import com.leyou.item.mapper.StockMapper;
import com.leyou.item.pojo.*;
import com.leyou.item.service.GoodsService;
import com.leyou.search.pojo.PageResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private CategoryServiceImpl categoryService;

    @Autowired
    private BrandsServiceImpl brandsService;

    @Autowired
    private GoodsDetailMapper goodsDetailMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;


    Logger logger = LoggerFactory.getLogger(GoodsServiceImpl.class);


    @Override
    public PageResult<SpuBo> querySpuByPageAndSort(String key, Boolean saleable, Integer page, Integer rows) {


        //开启分页
        PageHelper.startPage(page, rows);

        //添加判断条件
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();

        //判断是否有过滤
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("title", "%" + key + "%");
        }
        //判断是否上下架
        if (saleable != null) {
            criteria.andEqualTo("saleable", saleable);
        }
        //
        Page<Spu> spuPage = (Page<Spu>) goodsMapper.selectByExample(example);

        List<SpuBo> spuBoList = new ArrayList<>();
        List<Spu> spuList = new ArrayList<>();

        if (null != spuPage) {
            spuList = spuPage.getResult();
        }

        if (null != spuList) {
            spuList.forEach(spu -> {
                SpuBo spuBo = new SpuBo();
                //将spu赋值到spuBo
                BeanUtils.copyProperties(spu, spuBo);

                //查询cname
                List<String> names = this.categoryService.queryNamesById(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
                spuBo.setCname(StringUtils.join(names, "/"));
                //查询bname
                Brand brand = this.brandsService.queryBrandById(spu.getBrandId());
                spuBo.setBname(brand.getName());

                spuBoList.add(spuBo);

            });
        }

        return new PageResult<>(spuPage.getTotal(), new Long(spuPage.getPages()), spuBoList);
    }

    //新增商品
    @Override
    @Transactional
    public void saveGoods(SpuBo spuBo) {
        //1、新增商品表
        //1.1、是否上架
        spuBo.setSaleable(true);
        //1.2、创建时间
        spuBo.setCreateTime(new Date());
        //1.3、修改时间
        spuBo.setLastUpdateTime(spuBo.getCreateTime());
        //1.4、修改valid
        spuBo.setValid(true);
        goodsMapper.insert(spuBo);

        //2、新增商品详情
        spuBo.getSpuDetail().setSpuId(spuBo.getId());
        goodsDetailMapper.insert(spuBo.getSpuDetail());

        //获取skus
        List<Sku> skus = spuBo.getSkus();

        skus.forEach(sku -> {
            //3、新增商品库存
            //设置浏览器未提交参数
            sku.setSpuId(spuBo.getId());
            sku.setCreateTime(spuBo.getCreateTime());
            sku.setLastUpdateTime(spuBo.getCreateTime());
            skuMapper.insert(sku);

            //4、新增库存表
            Stock stock = new Stock();
            stock.setStock(sku.getStock());
            stock.setSkuId(sku.getId());
            stockMapper.insert(stock);

        });

        try {
            amqpTemplate.convertAndSend("item.insert",spuBo.getId());
        } catch (AmqpException e) {
            logger.error("商品新增消息发送异常");
            e.printStackTrace();
        }

    }

    //修改商品 TODO
    @Override
    @Transactional
    public void updateGoods(SpuBo spuBo) {
        //1、修改spu
        spuBo.setLastUpdateTime(new Date());
        goodsMapper.updateByPrimaryKeySelective(spuBo);

        //2、修改detail
        goodsDetailMapper.updateByPrimaryKeySelective(spuBo.getSpuDetail());

        //3、修改sku
        //查询数据库中存在的sku
        Sku getDbSku = new Sku();
        getDbSku.setSpuId(spuBo.getId());
        List<Sku> dbSkus = skuMapper.select(getDbSku);
//        List<Long> dbSkuIds = new ArrayList<>();
//        for (Sku dbSkuId : dbSkus) {
//            dbSkuIds.add(dbSkuId.getId());
//        }

        //3.1判断id是否存在
        //提交的sku集合
        List<Sku> skus = spuBo.getSkus();
        //要修改的skuid集合
        List<Long> updateSkuIds = new ArrayList<>();
        for (Sku sku : skus) {
            if (sku.getId() == null) {
                //设置浏览器未提交参数
                sku.setSpuId(spuBo.getId());
                sku.setCreateTime(new Date());
                sku.setLastUpdateTime(sku.getCreateTime());
                skuMapper.insert(sku);

                //新增库存表
                Stock stock = new Stock();
                stock.setStock(sku.getStock());
                stock.setSkuId(sku.getId());
                stockMapper.insert(stock);
            } else {
                sku.setLastUpdateTime(new Date());
                updateSkuIds.add(sku.getId());
            }
        }
        //判断需要修改或者下架的sku
        for (Sku dbSku : dbSkus) {
            if (!updateSkuIds.contains(dbSku.getId())) {
                dbSku.setEnable(false);
                dbSku.setLastUpdateTime(new Date());
                skuMapper.updateByPrimaryKeySelective(dbSku);
            } else {
                for (Sku sku : skus) {
                    if (dbSku.getId().longValue()==sku.getId().longValue()) {
                        skuMapper.updateByPrimaryKeySelective(sku);

                        //其他信息
                        Stock stock = new Stock();
                        stock.setStock(sku.getStock());
                        stock.setSkuId(dbSku.getId());
                        stockMapper.updateByPrimaryKeySelective(stock);
                    }
                }
            }
        }

        try {
            amqpTemplate.convertAndSend("item.update",spuBo.getId());
        } catch (AmqpException e) {
            logger.error("商品修改消息发送异常");
            e.printStackTrace();
        }

    }


    //修改商品回显查询
    @Override
    public SpuDetail querySpuDetailById(Long spuId) {
        SpuDetail spuDetail = this.goodsDetailMapper.selectByPrimaryKey(spuId);
        return spuDetail;
    }

    //修改商品回显查询
    @Override
    public List<Sku> querySkuById(Long id) {
        Sku sku = new Sku();
        sku.setSpuId(id);
        List<Sku> skus = skuMapper.select(sku);
//    stock回显
        Stock stock = new Stock();
        for (Sku skus1 : skus) {
            stock.setSkuId(skus1.getId());
            List<Stock> stocks = stockMapper.select(stock);
            for (Stock stock1 : stocks) {
                skus1.setStock(stock1.getStock());
            }
        }
        return skus;
    }

    @Override
    public Spu querySpuById(Long id) {
        Spu spu = goodsMapper.selectByPrimaryKey(id);
        return spu;

    }
}
