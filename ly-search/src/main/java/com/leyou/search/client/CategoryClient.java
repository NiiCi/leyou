package com.leyou.search.client;

import api.CategoryApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "item-service")
public interface CategoryClient extends CategoryApi {
}
