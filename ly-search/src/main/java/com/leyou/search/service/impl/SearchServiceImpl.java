package com.leyou.search.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.common.utils.JsonUtils;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecificationClient;
import com.leyou.search.dao.GoodsMapper;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.pojo.SearchResult;
import com.leyou.search.service.IndexService;
import com.leyou.search.service.SearchService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import pojo.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service("searchService")
@Log4j2
public class SearchServiceImpl implements SearchService {
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private SpecificationClient specificationClient;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private IndexService indexService;
    @Override
    public SearchResult search(SearchRequest searchRequest)throws Exception {
        //判断是否有所搜过滤条件,如果没有,直接返回null.不允许搜索全部商品
        if (StringUtils.isBlank(searchRequest.getKey())){
            return null;
        }
        String key = searchRequest.getKey();
        if (StringUtils.isBlank(key)){
            //如果用户没有搜索条件，我们可以默认的，或者返回null
            return  null;
        }
        log.info(searchRequest.toString());
        //1.创建es查询构造器
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();

        //对结果进行筛选
        //通过sourceFilter设置返回的结果字段,我们只需要id、skus、subTitle
        searchQueryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","skus","subTitle"}, null));

        //1.1基本查询 , 查询的字段为all，值为key
        searchQueryBuilder.withQuery(QueryBuilders.matchQuery("all",key).operator(Operator.AND));

        //1.2排序分页
        searchWithPageAndSort(searchQueryBuilder,searchRequest);

        //1.3过滤
        QueryBuilder query = buildBasicQuery(searchRequest);
        searchQueryBuilder.withQuery(query);

        //1.3 聚合
        String categoryAggName = "category"; //商品分类聚合名称
        String brandAggName = "brand"; //品牌聚合名称
        //对商品分类进行聚合
        searchQueryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        //对品牌进行聚合
        searchQueryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));
        //规格参数聚合

        //2.查询，获取结果
        AggregatedPage<Goods> pageInfo = (AggregatedPage<Goods>) goodsMapper.search(searchQueryBuilder.build());

        //3.解析查询结果
        //3.1 分页信息
        //总记录数
        Long total = pageInfo.getTotalElements();
        //总页数
        Long totalPage = (total + searchRequest.getSize() -1)/searchRequest.getSize();
        //3.2 商品分类的聚合结果
        List<Category> categories =  getCategoryAggResult(pageInfo.getAggregation(categoryAggName));
        //3.3 品牌的聚合结果
        List<Brand> brands = getBrandAggResult(pageInfo.getAggregation(brandAggName));

        //判断商品分类数量,判断是否需要对规格参数进行聚合
        List<Map<String,Object>> specs = null;
        if (categories.size() == 1){
            //如果分类只剩下一个，才进行规格参数聚合，即分类只有手机时，才进行规格参数聚合
            specs = getSpecs(categories.get(0).getId(), query);
        }
        //返回结果
        Page<Goods> result =  goodsMapper.search(searchQueryBuilder.build());
        //解析结果
        return new SearchResult(total,totalPage,result.getContent(),categories,brands,specs);
    }

    @Override
    public void createIndex(Long id) throws IOException {
        //查询spu
       Spu spu =  goodsClient.querySpuById(id);
       if (spu == null){
           log.error("索引对应的spu不存在，spuId: {}",id);
           //抛出异常，让消息回滚
           throw  new RuntimeException();
       }
       log.debug(objectMapper.writeValueAsString(spu));
       //查询 sku 信息
        List<Sku> skus = goodsClient.querySkuBySpuId(id);
       if (skus == null || skus.size() < 1){
           log.error("索引对应的skus不存在，spuId: {}",id);
           //抛出异常，让消息回滚
           throw  new RuntimeException();
       }
        log.debug(objectMapper.writeValueAsString(skus));
        //查询 spuDetail
        SpuDetail spuDetail = goodsClient.querySpuDetailById(id);
       if (spuDetail == null){
           log.error("索引对应的spuDetail不存在，spuId: {}",id);
           //抛出异常，让消息回滚
           throw  new RuntimeException();
       }
        log.debug(objectMapper.writeValueAsString(spuDetail));
        //查询商品分类名称
        List<String> nameList = categoryClient.queryNameByIds(Arrays.asList(spu.getCid1(),spu.getCid2(),spu.getCid3()));
        if (nameList == null || nameList.size()< 1 ){
            log.error("索引对应的分类不存在，spuId: {}",id);
            //抛出异常，让消息回滚
            throw  new RuntimeException();
        }
        log.debug(objectMapper.writeValueAsString(nameList));
        //查询规格参数
        List<SpecParam> specParamList = specificationClient.querySpecParams(null, spu.getCid3(), true, null);
        if(specParamList == null || specParamList.size()<1){
            log.error("索引对应的规格参数不存在，spuId: {}",id);
            //抛出异常，让消息回滚
            throw  new RuntimeException();
        }
        log.debug(objectMapper.writeValueAsString(specParamList));

        //我们需要的数据
        // spuid、子标题、品牌id、分类id1、分类id2、分类id3、价格的数组、商品特殊信息集合、json格式的sku信息、map类型的spec信息
        // 所有需要被搜索的信息，包含标题，分类信息，品牌名等

        List<Long> priceList = new ArrayList<>();
        List<Map<String, Object>> skuJsonList = new ArrayList<>();
        skus.forEach(s -> {
            //价格
            priceList.add(s.getPrice());
            Map<String, Object> map = new ConcurrentHashMap<>();
            //skuid、标题、图片、价格
            map.put("id", s.getId());
            map.put("title", s.getTitle());
            map.put("image", StringUtils.isBlank(s.getImages()) ? "" : s.getImages().split(",")[0]);
            map.put("price", s.getPrice());
            skuJsonList.add(map);
        });

        //处理规格参数
        Map<Long, String> genericMap = JsonUtils.parseMap(spuDetail.getGenericSpec(), Long.class, String.class);
        Map<Long, List<String>> specialMap = JsonUtils.nativeRead(spuDetail.getSpecialSpec(), new TypeReference<Map<Long, List<String>>>() {
        });

        Map<String, Object> specs = new ConcurrentHashMap<>();
        specParamList.forEach(s -> {
            if (s.getGeneric()) {
                //通用参数
                String value = genericMap.get(s.getId());
                if (s.getNumeric()){
                    //数值类型,需要存储一个分段
                    value = indexService.chooseSegment(value, s);
                }
                specs.put(s.getName(),value);
            }else{
                //特有参数
                specs.put(s.getName(),specialMap.get(s.getId()));
            }
        });

        Goods goods = new Goods();
        goods.setBrandId(spu.getBrandId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setCreateTime(spu.getCreateTime());
        goods.setId(spu.getId());
        goods.setSubTitle(spu.getSubTitle());
        //搜索条件 拼接: 标题、分类、品牌
        goods.setAll(spu.getTitle()+" "+StringUtils.join(nameList," "));
        goods.setPrice(priceList);
        goods.setSkus(JsonUtils.serialize(skuJsonList));
        goods.setSpecs(specs);
        log.debug(objectMapper.writeValueAsString(goods));
        // 保存数据到索引库
        goodsMapper.save(goods);
    }

    @Override
    public void deleteIndex(Long id) throws IOException{
        goodsMapper.deleteById(id);
    }

    //构建基本查询条件
    private QueryBuilder buildBasicQuery(SearchRequest searchRequest) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        //基本查询条件
        queryBuilder.must(QueryBuilders.matchQuery("all",searchRequest.getKey()).operator(Operator.AND));
        //过滤条件构建器
        BoolQueryBuilder filterQueryBuilder = QueryBuilders.boolQuery();
        //整理过滤条件
        Map<String,String> filter = searchRequest.getFilter();
        filter.entrySet().forEach(s->{
            String key = s.getKey();
            String value = s.getValue();
            //商品分类和品牌进行特殊处理
            if (key != "cid3" && key != "brandId"){
                key = "specs." + key + ".keyword";
            }
            // 字符串类型，进行term查询
            filterQueryBuilder.must(QueryBuilders.termQuery(key,value));
        });
        //添加过滤条件
        queryBuilder.filter(filterQueryBuilder);
        return queryBuilder;
    }

    /**
     * 聚合规格参数
     * @param id
     * @param searchQueryBuilder
     * @return
     */
    private List<Map<String,Object>> getSpecs(Long cid, QueryBuilder query) {
        try {
            //根据分类查询规格
            List<SpecParam> params = specificationClient.querySpecParams(null,cid,true,null);
            //创建集合，保存规格过滤条件
            List<Map<String,Object>> specs = new ArrayList<>();

            NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
            queryBuilder.withQuery(query);
            //聚合规格参数
            params.forEach(p->{
                String key = p.getName();
                    queryBuilder.addAggregation(AggregationBuilders.terms(key).field("specs." + key + ".keyword"));
            });
            //查询
            Map<String,Aggregation> aggs = elasticsearchTemplate.query(queryBuilder.build(),SearchResponse::getAggregations).asMap();
            log.info(aggs);
            //解析聚合结果
            params.forEach(param->{
                Map<String,Object> spec = new HashMap<>();
                String key = param.getName();
                spec.put("k",key);
                StringTerms terms = (StringTerms) aggs.get(key);
                spec.put("options",terms.getBuckets().stream().map(StringTerms.Bucket::getKeyAsString));
                specs.add(spec);
            });
            return specs;
        } catch (Exception e) {
            log.error("规格聚合出现异常: ",e);
            return null;
        }
    }

    /**
     * 解析商品分类聚合结果
     * @param aggregation
     * @return
     */
    private List<Category> getCategoryAggResult(Aggregation aggregation) {
        try {
            List<Category> categoryList = new ArrayList<>();
            // 向下转型为LongTerms
            LongTerms categoryAgg = (LongTerms) aggregation;
            List<Long> cids = new ArrayList<>();
            //获取桶的集合
            categoryAgg.getBuckets().parallelStream().forEach(bucket->{
                //获取商品的分类cid,并添加到cids集合中
                cids.add(bucket.getKeyAsNumber().longValue());
            });
            //根据id查询分类名称
            List<String> names = categoryClient.queryNameByIds(cids);
            for (int i = 0; i < names.size(); i++) {
                Category c = new Category();
                c.setId(cids.get(i));
                c.setName(names.get(i));
                categoryList.add(c);
            }
            return categoryList;
        } catch (Exception e) {
            log.error("分类聚合出现异常: ",e);
            return null;
        }
    }


    /**
     * 解析品牌聚合结果
     * @param aggregation
     * @return
     */
    private List<Brand> getBrandAggResult(Aggregation aggregation) {
        try {
            List<Brand> brandList = new ArrayList<>();
            List<Long> bids = new ArrayList<>();
            // 向下转型为LongTerms
            LongTerms brandAgg = (LongTerms) aggregation;
            //获取桶的集合
            brandAgg.getBuckets().parallelStream().forEach(bucket->{
                //获取品牌bid,并添加到bids集合中
                bids.add(bucket.getKeyAsNumber().longValue());
            });
            // 根据id查询品牌
            return brandClient.queryBrandByIds(bids);
        } catch (Exception e) {
            log.error("品牌聚合出现异常",e);
            return null;
        }
    }

    /**
     * 分页排序
     * @param searchQueryBuilder
     * @param searchRequest
     */
    private void searchWithPageAndSort(NativeSearchQueryBuilder searchQueryBuilder, SearchRequest searchRequest) {
        // 准备分页参数
        //page 从0开始
        Integer page = searchRequest.getPage() - 1;
        Integer size = searchRequest.getSize();
        //1.分页
        searchQueryBuilder.withPageable(PageRequest.of(page,size));
        //2.排序
        String sortBy = searchRequest.getSortBy();
        Boolean desc = searchRequest.getDescending();
        if (StringUtils.isNotBlank(sortBy)){
            //如果不为空,则进行排序
            searchQueryBuilder.withSort(SortBuilders.fieldSort(sortBy).order(desc ? SortOrder.DESC:SortOrder.ASC));
        }
    }
}
