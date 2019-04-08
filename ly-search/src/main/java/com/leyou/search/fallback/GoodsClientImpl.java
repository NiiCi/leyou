package com.leyou.search.fallback;

import com.leyou.common.pojo.PageResult;
import com.leyou.search.client.GoodsClient;
import pojo.Sku;
import pojo.Spu;
import pojo.SpuBo;
import pojo.SpuDetail;

import java.util.List;

public class GoodsClientImpl implements GoodsClient {
    @Override
    public PageResult<SpuBo> querySpuByPage(Integer page, Integer rows, Boolean saleable, String key) {
        return null;
    }

    @Override
    public SpuDetail querySpuDetailById(Long id) {
        return null;
    }

    @Override
    public List<Sku> querySkuBySpuId(Long id) {
        return null;
    }

    @Override
    public Spu querySpuById(Long id) {
        return null;
    }

    @Override
    public Sku querySkuById(Long id) {
        return null;
    }
}
