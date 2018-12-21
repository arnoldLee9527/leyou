package com.leyou.TestDemo;

import com.leyou.ElasticsearchApplication;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuBo;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.PageResult;
import com.leyou.search.repository.GoodsRepository;
import com.leyou.search.service.IndexService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ElasticsearchApplication.class)
public class Testindex {
    @Autowired
    private GoodsRepository goodsRepository;
    @Autowired
    private IndexService indexService;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private GoodsClient goodsClient;

    @Test
    public void insertindex() {
        elasticsearchTemplate.createIndex(Goods.class);
        elasticsearchTemplate.putMapping(Goods.class);
    }

    @Test
    public void indexServiceTest() {

        int i = 1;

        while (true){

            PageResult<SpuBo> spuBoPageResult = goodsClient.querySpuByPageAndSort(null, true, i, 50);

            i++;

            if (spuBoPageResult==null){
                break;
            }

            List<SpuBo> spuBos = spuBoPageResult.getItems();

            List<Goods> goodsList = new ArrayList<>();
            for (SpuBo spuBo : spuBos) {

                goodsList.add(indexService.buildGoods(spuBo));
            }

            goodsRepository.saveAll(goodsList);



        }

    }


}
