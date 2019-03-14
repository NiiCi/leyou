package com.leyou.search.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
/**
 * @Document 代表声明该类为es的文档
 * goods 代表库名
 * docs 代表表名
 * shards 代表分片数量
 * replicas 代表副本数量
 */
@Document(indexName = "goods", type = "docs", shards = 1, replicas = 0)
public class Goods {
    /**
     * spuId
     */
    @Id
    private Long id;
    /**
     * 所有需要被搜索的信息，包含标题，分类信息，品牌等
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String all;
    /**
     * 子标题
     */
    @Field(type = FieldType.Keyword, index = false)
    private String subTitle;
    /**
     * 品牌id
     */
    private Long brandId;
    /**
     * 一级分类id
     */
    private Long cid1;
    /**
     * 二级分类id
     */
    private Long cid2;
    /**
     * 三级分类id
     */
    private Long cid3;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 价格
     */
    private List<Long> price;
    /**
     * json格式的sku信息
     * index = false  代表不需要索引，则不能被搜索
     */
    @Field(type = FieldType.Keyword, index = false)
    private String skus;
    /**
     * 可搜索的规格参数，key是参数名，值是参数值
     */
    private Map<String,Object> specs;
}
