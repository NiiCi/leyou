package com.leyou.item.sevice.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.dao.BrandDao;
import com.leyou.item.dao.CategoryDao;
import com.leyou.item.sevice.BrandService;
import com.netflix.discovery.converters.Auto;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pojo.Brand;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.util.ArrayList;
import java.util.List;


@Service(value = "brandService")
@Log4j2
public class BrandServiceImpl implements BrandService {
    @Autowired
    private BrandDao brandDao;
    @Autowired
    private CategoryDao categoryDao;

    @Override
    public PageResult<Brand> queryBrandByPageAndSort(Integer page, Integer rows, String sortBy, Boolean desc, String key) throws Exception {
        //分页拦截
        PageHelper.startPage(page, rows);
        //过滤
        Example example = new Example(Brand.class);
        if (StringUtil.isNotEmpty(key)) {
            example.createCriteria().andLike("name", "%" + key + "%").orEqualTo("letter", key);
        }
        if (StringUtil.isNotEmpty(sortBy)){
            //排序
            String orderByClause = sortBy + (desc ? " DESC":" ASC");
            example.setOrderByClause(orderByClause);
        }
        //查询
        Page<Brand> pageInfo = (Page<Brand>) brandDao.selectByExample(example);
        //返回结果
        return new PageResult<>(pageInfo.getTotal(),pageInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBrand(Brand brand, List<Long> cids) throws Exception{
        log.info(brand.toString());
        brandDao.insertSelective(brand);
        for (Long cid : cids) {
            brandDao.insertCategoryBrand(cid, brand.getId());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBrand(Brand brand, List<Long> cids) throws Exception{
        //修改品牌信息
        brandDao.updateByPrimaryKey(brand);
        //修改分类信息
        List<Long> cidList = categoryDao.queryCategoryIdbyBrandId(brand.getId());
        List<Long> addCidList = new ArrayList<>();
        List<Long> delCidList = new ArrayList<>();

       //先删除,再添加
        categoryDao.deleteCategoryBrand(brand.getId());

        cids.stream().forEach((s)->{
           brandDao.insertCategoryBrand(s,brand.getId());
        });
    }

    @Override
    public void deleteBrand(long bid) throws Exception {
        brandDao.deleteByPrimaryKey(bid);
    }

    @Override
    public List<Brand> queryBrandByCid(Long cid) throws Exception{
        return brandDao.queryBrandByCid(cid);
    }
}
