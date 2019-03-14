package com.leyou.search.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.common.utils.JsonUtils;
import com.leyou.common.utils.NumberUtils;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecificationClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.service.IndexService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pojo.Sku;
import pojo.SpecParam;
import pojo.Spu;
import pojo.SpuDetail;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service("indexService")
public class IndexServiceImpl implements IndexService {
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private SpecificationClient specificationClient;

    @Override
    public Goods buildGoods(Spu spu) {
        //获取spuid
        Long id = spu.getId();
        //1、准备数据

        //商品特殊信息
        List<Sku> skuList = goodsClient.querySkuBySpuId(id);
        //商品详细信息
        SpuDetail detail = goodsClient.querySpuDetailById(id);
        //商品分类
        List<String> nameList = categoryClient.queryNameByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        //查询规格参数
        List<SpecParam> specParamList = specificationClient.querySpecParams(null, spu.getCid3(), null, true);
        // TODO查询品牌名称

        //我们需要的数据
        // spuid、子标题、品牌id、分类id1、分类id2、分类id3、价格的数组、商品特殊信息集合、json格式的sku信息、map类型的spec信息
        // 所有需要被搜索的信息，包含标题，分类信息，品牌名等
        //处理sku
        List<Long> priceList = new ArrayList<>();
        List<Map<String, Object>> skuJsonList = new ArrayList<>();
        skuList.forEach(s -> {
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
        Map<Long, String> genericMap = JsonUtils.parseMap(detail.getGenericSpec(), Long.class, String.class);
        Map<Long, List<String>> specialMap = JsonUtils.nativeRead(detail.getGenericSpec(), new TypeReference<Map<Long, List<String>>>() {
        });

        Map<String, Object> specs = new ConcurrentHashMap<>();
        specParamList.forEach(s -> {
            if (s.getGeneric()) {
                //通用参数
                String value = genericMap.get(s.getId());
                if (s.getNumeric()){
                    //数值类型,需要存储一个分段
                    value = this.chooseSegment(value, s);
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
        return goods;
    }

    private String chooseSegment(String value, SpecParam spec) {
        Double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        // 数值段 为{0-1,1-2}的json形式，进行分割
        String[] segments =  spec.getSegments().split(",");
        for (String segment : segments) {
            String[] segs = segment.split("-");
            //获取数值范围
            Double begin = NumberUtils.toDouble(segs[0]);
            Double end = Double.MAX_VALUE;
            if (segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            //判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + spec.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + spec.getUnit() + "以下";
                }else{
                    result = segment + spec.getUnit();
                }
                break;
            }
        }
        return result ;
    }

}
