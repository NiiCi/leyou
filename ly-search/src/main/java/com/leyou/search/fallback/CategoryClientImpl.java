package com.leyou.search.fallback;

import com.leyou.search.client.CategoryClient;

import java.util.ArrayList;
import java.util.List;

public class CategoryClientImpl implements CategoryClient {
    @Override
    public List<String> queryNameByIds(List<Long> ids) {
        return new ArrayList<>();
    }
}
