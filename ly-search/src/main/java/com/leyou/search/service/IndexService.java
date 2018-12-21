package com.leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.pojo.SpuBo;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.ItemClient;
import com.leyou.search.client.SpecClient;
import com.leyou.search.pojo.Goods;
import com.leyou.utils.JsonUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class IndexService {
    @Autowired
    private ItemClient itemClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecClient specClient;

    //保存搜索值
    //把传入的spu转换为goods
    public Goods buildGoods(SpuBo spu){

        Goods goods = new Goods();

        //将spu的信息拷贝
        BeanUtils.copyProperties(spu,goods);
        //其他spu中没有的变量

        //all：用来进行全文检索的字段，里面包含标题、商品分类信息
        List<String> strings = itemClient.queryNamesById(Arrays.asList(spu.getCid1(),spu.getCid2(),spu.getCid3()));
        goods.setAll(spu.getTitle()+StringUtils.join(strings," "));

        //price：价格数组，是所有sku的价格集合。方便根据价格进行筛选过滤
        //skus：用于页面展示的sku信息，不索引，不搜索。包含skuId、image、price、title字段
        List<Sku> skus = goodsClient.querySkuById(spu.getId());

        //将价格单独取出，便于展示
        List<Long> prices = new ArrayList<>();

        List<Map<String,Object>> skuList = new ArrayList<>();


        for (Sku sku : skus) {
            Map<String,Object> skuMap = new HashMap<>();
            //单独存放一份价格
            prices.add(sku.getPrice());


            //存储sku
            skuMap.put("id",sku.getId());
            skuMap.put("price",sku.getPrice());
            skuMap.put("title",sku.getTitle());
            skuMap.put("image",(StringUtils.isNotBlank(sku.getImages())?sku.getImages().split(",")[0]:""));
            skuList.add(skuMap);

        }
        //将价格存入goods
        goods.setPrice(prices);
        //将skus存入goods
        goods.setSkus(JsonUtils.serialize(skuList));

        //所有可搜索规格参数的集合。key是参数名，值是参数值。

        //获取全部可搜索的规格参数
        List<SpecParam> specParams = specClient.querySpecParams(null, spu.getCid3(), null, true);

        //获取规格参数详情
        SpuDetail spuDetail = goodsClient.querySpuDetailById(spu.getId());

        //获取通用规格参数详情
        String geneSpec = spuDetail.getGenericSpec();
        //将通用规格参数转换为map
        Map<Long,Object> genericSpec = JsonUtils.parseMap(geneSpec,Long.class,Object.class);

        //获取特殊规格参数详情
        String specSpec = spuDetail.getSpecialSpec();
        //转换特殊规格参数
        Map<Long,List<String>> specialSpec = JsonUtils.nativeRead(specSpec, new TypeReference<Map<Long, List<String>>>() {
        });

        Map<String,Object> specs = new HashMap<>();

        for (SpecParam specParam : specParams) {

            //获取key
            String name = specParam.getName();
            Long paramId = specParam.getId();

            Object value = null;

            if (specParam.getGeneric()){

                value = genericSpec.get(paramId);



                if (specParam.getNumeric()){
                    //数值类型需要加分段
                    value = chooseSegment(value.toString(),specParam);
                }

            }else {
               value = specialSpec.get(paramId);

            }

            if (null==value){
                value="其他";
            }
            specs.put(name,value);
        }

        goods.setSpecs(specs);

        return goods;
    }

    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else{
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }



}
