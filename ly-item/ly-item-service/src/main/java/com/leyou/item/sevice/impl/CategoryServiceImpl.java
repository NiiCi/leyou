package com.leyou.item.sevice.impl;

import com.leyou.item.dao.CategoryDao;
import com.leyou.item.sevice.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import pojo.Category;

import java.util.ArrayList;
import java.util.List;

@Service(value = "categoryService")
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryDao categoryDao;

    /**
     * 根据parentId查询子类目
     * @param pid
     * @return
     * @throws Exception
     */
    @Override
    public List<Category> queryCategoryListByParentId(Long pid) throws Exception {
        Category record = new Category();
        record.setParentId(pid);
        return categoryDao.select(record);
    }

    @Override
    public List<Category> queryByBrandId(Long bid) throws Exception {
        List<Category> list = categoryDao.queryByBrandId(bid);
        return list;
    }

    @Override
    public List<Long> queryCategoryIdByBrandId(Long bid) throws Exception {
        return categoryDao.queryCategoryIdbyBrandId(bid);
    }

    @Override
    public List<String> queryNameByIds(List<Long> ids) throws Exception {
        List<Category> list = categoryDao.selectByIdList(ids);
        List<String> nameList = new ArrayList<>();
        list.forEach(names->{
            nameList.add(names.getName());
        });
        return nameList;
    }
}
