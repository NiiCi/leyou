package com.leyou.search.pojo;


import com.leyou.common.pojo.PageResult;
import pojo.Brand;
import pojo.Category;

import java.util.List;

/**
 * 搜索过滤结果类,继承PageResult类，并拓展
 */
public class SearchResult extends PageResult {
    private List<Category> categories;
    private List<Brand> brands;

    public SearchResult(Long total, Long totalPage, List items, List<Category> categories, List<Brand> brands) {
        super(total, totalPage, items);
        this.categories = categories;
        this.brands = brands;
    }
}
