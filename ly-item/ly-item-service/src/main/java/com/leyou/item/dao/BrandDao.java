package com.leyou.item.dao;


import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import pojo.Brand;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

    public interface BrandDao extends Mapper<Brand>,SelectByIdListMapper<Brand,Long> {
        @Insert("INSERT INTO tb_category_brand (category_id, brand_id) VALUES (#{cid},#{bid})")
        public int insertCategoryBrand(@Param("cid") Long cids,  @Param("bid") Long id);

        @Update("UPDATE tb_category_brand SET category_id = #{cid},brand_id = #{bid}")
        public int updateCategoryBrand(@Param("cid") Long cid, @Param("bid") Long id);

        @Select("select id,name,image,letter from tb_brand where id in " +
                "(select brand_id from tb_category_brand where category_id = #{cid})")
        List<Brand> queryBrandByCid(Long cid);
    }
