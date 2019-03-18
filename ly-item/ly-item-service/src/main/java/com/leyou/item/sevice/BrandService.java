package com.leyou.item.sevice;

import com.leyou.common.pojo.PageResult;
import pojo.Brand;

import java.util.List;

public interface BrandService {
    /**
     * 查询品牌列表
     * @param page 当前夜
     * @param rows 每页记录数
     * @param sortBy
     * @param desc 是否降序
     * @param key 搜索关键词
     * @return
     */
    public PageResult<Brand> queryBrandByPageAndSort(Integer page,Integer rows,String sortBy,Boolean desc,String key) throws Exception;

    /**
     * 保存品牌信息
     * @param brand
     * @param cids
     */
    public void saveBrand(Brand brand, List<Long> cids) throws Exception;

    /**
     * 修改品牌信息
     * @param brand
     * @param cids
     */
    public void updateBrand(Brand brand, List<Long> cids) throws Exception;

    /**
     * 删除品牌信息
     * @param bid
     * @throws Exception
     */
    public void deleteBrand(long bid) throws Exception;

    /**
     * 通过分类id查询品牌
     * @param cid
     * @return
     */
    public List<Brand> queryBrandByCid(Long cid) throws Exception;

    /**
     * 通过多个品牌id 查询品牌信息
     * @param ids
     * @return
     */
    List<Brand> queryBrandByIds(List<Long> ids);
}
