package com.leyou.item.sevice;

import com.leyou.item.dao.CategoryDao;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pojo.Category;

import java.util.List;

public interface CategoryService {
    /**
     * 根据parentId查询子类目
     * @param pid
     * @return
     */
   public List<Category> queryCategoryListByParentId(Long pid) throws Exception;

    /**
     * 通过品牌Id查询商品分类
     * @param bid
     * @return
     */
   public List<Category> queryByBrandId(Long bid) throws Exception;

    /**
     * 通过品牌Id查询分类Id
     * @param bid
     * @return
     * @throws Exception
     */
    public List<Long> queryCategoryIdByBrandId(Long bid) throws Exception;


    /**
     * 通过分类id查询分类名称
     * @param longs
     * @return
     */
    public List<String> queryNameByIds(List<Long> ids) throws Exception;
}
