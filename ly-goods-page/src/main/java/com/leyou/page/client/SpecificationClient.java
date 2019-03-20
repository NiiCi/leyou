package com.leyou.page.client;

import api.SpecificationApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "item-service")
public interface SpecificationClient  extends SpecificationApi {
}
