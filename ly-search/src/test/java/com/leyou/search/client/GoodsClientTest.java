package com.leyou.search.client;

import com.leyou.search.SearchApplication;
import com.leyou.search.pojo.Goods;
import com.netflix.discovery.converters.Auto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SearchApplication.class)
public class GoodsClientTest {
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Test
    public void createIndex(){
        //创建索引
        elasticsearchTemplate.createIndex(Goods.class);
        //创建映射关系
        elasticsearchTemplate.putMapping(Goods.class);
    }
}