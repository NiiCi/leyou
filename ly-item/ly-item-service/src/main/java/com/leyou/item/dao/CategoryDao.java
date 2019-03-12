package com.leyou.item.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import pojo.Category;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CategoryDao extends Mapper<Category>,SelectByIdListMapper<Category,Long> {

    /**
     * 通过品牌id查询分类信息
     * @param bid
     * @return
     */
    @Select("select * from tb_category where id in (select category_id from tb_category_brand where brand_id = #{bid})")
    public List<Category> queryByBrandId(Long bid);

    /**
     * 通过品牌id 查询分类id
     * @param bid
     * @return
     */
    @Select("select category_id from tb_category_brand where brand_id = #{bid}")
    public List<Long> queryCategoryIdbyBrandId(Long bid);

    /**
     * 删除品牌分类关联表信息
     * @param bid
     * @return
     */
    @Delete("delete from tb_category_brand where brand_id = #{bid}")
    public int deleteCategoryBrand(Long bid);

    /**
     * 通过分类id查询分类名称
     * @param id
     * @return
     */
    @Select("select name from tb_category where id = #{id}")
    public String queryNameByIds(Long id);
}
