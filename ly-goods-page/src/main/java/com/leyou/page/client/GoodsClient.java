package com.leyou.page.client;

import api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "item-service")
public interface GoodsClient  extends GoodsApi {
}
