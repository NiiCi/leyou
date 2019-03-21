package com.leyou.search.client;

import com.leyou.common.pojo.PageResult;
import com.leyou.search.SearchApplication;
import com.leyou.search.dao.GoodsMapper;
import com.leyou.search.pojo.Goods;
import com.leyou.search.service.IndexService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import pojo.SpuBo;

import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SearchApplication.class)
public class GoodsClientTest {
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private IndexService indexService;
    @Autowired
    private GoodsMapper goodsMapper;

    @Test
    public void createIndex(){
        //创建索引
        elasticsearchTemplate.createIndex(Goods.class);
        //创建映射关系
        elasticsearchTemplate.putMapping(Goods.class);
    }

    @Test
    public void loadData(){
        int page = 1;
        int rows =100;
        int size = 0;
        do {
            //查询spu
            PageResult<SpuBo> spuList =goodsClient.querySpuByPage(10,5,true,null);
            //spu 转 goods
            List<SpuBo> spus =  spuList.getItems();
            List<Goods> goodsList = spus.parallelStream().map(indexService::buildGoods).collect(Collectors.toList());
            //把goodsList 插入到索引库中
            goodsMapper.saveAll(goodsList);
            size = spus.size();
            page++;
        }while (size == 100);

    }
}