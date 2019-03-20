package com.leyou.item.sevice.impl;

import com.leyou.item.dao.SpecGroupDao;
import com.leyou.item.dao.SpecParamDao;
import com.leyou.item.sevice.SpecificationService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pojo.SpecGroup;
import pojo.SpecParam;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

@Service("specificationService")
@Log4j2
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

    @Override
    public List<SpecGroup> querySpecsByCid(Long cid) throws Exception {
        //查询规格组
        List<SpecGroup> groups = querySpecGroups(cid);
        SpecParam specParam = new SpecParam();
        groups.parallelStream().forEach(s->{
            try {
                //通过规格组id，gid查询组内规格参数
                s.setParams(querySpecParams(s.getId(),null,true,null));
            } catch (Exception e) {
                log.error(e.getMessage(),e);
            }
        });
        return groups;
    }
}
