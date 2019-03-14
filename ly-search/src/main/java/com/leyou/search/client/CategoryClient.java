package com.leyou.search.client;

import api.CategoryApi;
import com.leyou.search.fallback.CategoryClientImpl;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "item-service",fallback = CategoryClientImpl.class,configuration = FeignConfig.class)
public interface CategoryClient extends CategoryApi {
}
