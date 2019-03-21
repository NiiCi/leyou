package com.leyou.page.service.impl;

import com.leyou.page.client.BrandClient;
import com.leyou.page.client.CategoryClient;
import com.leyou.page.client.GoodsClient;
import com.leyou.page.client.SpecificationClient;
import com.leyou.page.service.GoodsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import pojo.*;

import java.util.*;

@Service(value = "goodsService")
@Log4j2
public class GoodsServiceImpl implements GoodsService {
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private SpecificationClient specificationClient;
    @Autowired
    private CategoryClient categoryClient;
    @Override
    public Map<String, Object> loadModel(Long id) throws Exception {
        if (id == 0){
            return null;
        }
        //模型数据
        Map<String,Object> modelMap = new HashMap<>();
        try {
            //查询spu
            Spu spu = goodsClient.querySpuById(id);
            //查询spuDetail
            SpuDetail spuDetail = goodsClient.querySpuDetailById(id);
            //查询sku
            List<Sku> skuList = goodsClient.querySkuBySpuId(id);
            //填装模型数据
            modelMap.put("spu",spu);
            modelMap.put("spuDetail",spuDetail);
            modelMap.put("skus",skuList);
            //准备商品分类
            List<Category> categoryList = getCategories(spu);
            if (!CollectionUtils.isEmpty(categoryList)){
                modelMap.put("categories",categoryList);
            }
            //准备品牌数据
            List<Brand> brandList = brandClient.queryBrandByIds(Arrays.asList(spu.getBrandId()));
            if (!CollectionUtils.isEmpty(brandList)){
                modelMap.put("brand",brandList.get(0));
            }
            //查询规格组及组内参数
            List<SpecGroup> specGroupList = specificationClient.querySpecsByCid(spu.getCid3());
            modelMap.put("groups",specGroupList);

            //查询商品分类下的特有规格参数
            List<SpecParam> specParamList = specificationClient.querySpecParams(null,spu.getCid3(),null,false);
            //处理成id : name 格式的数据
            Map<Long,String> paramMap = new HashMap<>();
            specParamList.parallelStream().forEach(s->{
                paramMap.put(s.getId(),s.getName());
            });
            modelMap.put("paramMap",paramMap);
        } catch (Exception e) {
            log.error("加载商品数据出错,spuId:{}", id, e);
        }
        return modelMap;
    }

    /**
     * 根据商品信息获取所有分类
     * @param spu
     * @return
     */
    private List<Category> getCategories(Spu spu) {
        try {
            List<String> names = this.categoryClient.queryNameByIds(
                    Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            Category c1 = new Category();
            c1.setName(names.get(0));
            c1.setId(spu.getCid1());

            Category c2 = new Category();
            c2.setName(names.get(1));
            c2.setId(spu.getCid2());

            Category c3 = new Category();
            c3.setName(names.get(2));
            c3.setId(spu.getCid3());

            return Arrays.asList(c1, c2, c3);
        } catch (Exception e) {
            log.error("查询商品分类出错，spuId：{}", spu.getId(), e);
        }
        return null;
    }
}
