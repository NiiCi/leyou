package com.leyou.item.sevice;

import pojo.SpecGroup;
import pojo.SpecParam;

import java.util.List;

public interface SpecificationService {

    public List<SpecGroup> querySpecGroups(Long cid) throws Exception;

    public int insertSpecGroup(SpecGroup specGroup) throws Exception;

    public int updateSpecGroup(SpecGroup specGroup) throws Exception;

    public int deleteSpecGroup(Long id) throws Exception;

    public List<SpecParam> querySpecParams(Long gid,Long cid,Boolean searching,Boolean generic) throws Exception;

    public int insertSpecParam(SpecParam specParam) throws Exception;

    public int updateSpecParam(SpecParam specParam) throws Exception;

    public int deleteSpecParam(Long id) throws Exception;
}
