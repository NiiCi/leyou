package com.leyou.search.client;

import api.BrandApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "item-service",configuration = FeignConfig.class)
public interface BrandClient extends BrandApi {
}
