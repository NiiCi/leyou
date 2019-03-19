package com.leyou.search.pojo;


import com.leyou.common.pojo.PageResult;
import lombok.Data;
import pojo.Brand;
import pojo.Category;

import java.util.List;
import java.util.Map;

/**
 * 搜索过滤结果类,继承PageResult类，并拓展
 */
@Data
public class SearchResult extends PageResult<Goods> {
    /**
     * 返回的分类过滤条件
     */
    private List<Category> categories;
    /**
     * 返回的品牌过滤条件
     */
    private List<Brand> brands;
    /**
     * 返回的规格参数过滤条件
     */
    private List<Map<String,Object>> specs;

    public SearchResult(Long total, Long totalPage, List<Goods> items,
                        List<Category> categories, List<Brand> brands,List<Map<String,Object>> specs) {
        super(total, totalPage, items);
        this.categories = categories;
        this.brands = brands;
        this.specs = specs;
    }
}
