package com.leyou.search.client;

import api.GoodsApi;
import com.leyou.search.fallback.GoodsClientImpl;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "item-service",fallback = GoodsClientImpl.class)
public interface GoodsClient extends GoodsApi {
}
