package com.leyou.item.sevice.impl;

import com.leyou.item.dao.SpecGroupDao;
import com.leyou.item.dao.SpecParamDao;
import com.leyou.item.sevice.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pojo.SpecGroup;
import pojo.SpecParam;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

@Service("specificationService")
public class SpecificationServiceImpl implements SpecificationService {
    @Autowired
    private SpecGroupDao specGroupDao;
    @Autowired
    private SpecParamDao specParamDao;
    @Override
    public List<SpecGroup> querySpecGroups(Long cid) throws Exception {
        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);
        return specGroupDao.select(specGroup);
    }

    @Override
    public int insertSpecGroup(SpecGroup specGroup) throws Exception {
        return  specGroupDao.insertSelective(specGroup);
    }

    @Override
    public int updateSpecGroup(SpecGroup specGroup) throws Exception {
        return specGroupDao.updateByPrimaryKey(specGroup);
    }

    @Override
    public int deleteSpecGroup(Long id) throws Exception {
        return specGroupDao.deleteByPrimaryKey(id);
    }

    @Override
    public List<SpecParam> querySpecParams(Long gid,Long cid,Boolean searching,Boolean generic) throws Exception {
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        specParam.setCid(cid);
        specParam.setSearching(searching);
        specParam.setGeneric(generic);
        return specParamDao.select(specParam);
    }

    @Override
    public int insertSpecParam(SpecParam specParam) throws Exception {
        return specParamDao.insertSelective(specParam);
    }

    @Override
    public int updateSpecParam(SpecParam specParam) throws Exception {
        return specParamDao.updateByPrimaryKey(specParam);
    }

    @Override
    public int deleteSpecParam(Long id) throws Exception {
        return specParamDao.deleteByPrimaryKey(id);
    }
}
