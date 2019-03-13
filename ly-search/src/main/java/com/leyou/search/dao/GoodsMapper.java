package com.leyou.search.dao;

import com.leyou.search.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface GoodsMapper extends ElasticsearchRepository<Goods,Long> {
}
