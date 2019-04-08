package com.leyou.item.sevice;

import com.leyou.common.pojo.PageResult;
import pojo.Sku;
import pojo.Spu;
import pojo.SpuBo;
import pojo.SpuDetail;

import java.util.List;

public interface GoodsService {
    public PageResult<SpuBo> querySpuByPage(Integer page, Integer rows, Boolean saleable, String key) throws Exception;

    public void saveGoods(SpuBo spuBo) throws Exception;

    public SpuDetail querySpuDetailById(Long id) throws Exception;

    public List<Sku> querySkuBySpuId(Long id) throws Exception;

    public void updateGoods(SpuBo spuBo) throws Exception;

    public void deleteGoods(Long id) throws Exception;

    public Spu querySpuById(Long id) throws Exception;

    public Sku querySkuById(Long id) throws Exception;
}
