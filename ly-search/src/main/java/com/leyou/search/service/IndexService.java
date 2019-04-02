package com.leyou.search.service;

import com.leyou.search.pojo.Goods;
import pojo.SpecParam;
import pojo.Spu;

public interface IndexService {
    public Goods buildGoods(Spu spu);
    public String chooseSegment(String value, SpecParam spec);
}
