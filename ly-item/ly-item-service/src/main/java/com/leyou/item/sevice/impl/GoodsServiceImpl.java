package com.leyou.item.sevice.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.dao.*;
import com.leyou.item.sevice.CategoryService;
import com.leyou.item.sevice.GoodsService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import pojo.*;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service("goodsService")
@Log4j2
public class GoodsServiceImpl implements GoodsService {
    @Autowired
    private SpuDao spuDao;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BrandDao brandDao;
    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private SpuDetailDao spuDetailDao;
    @Autowired
    private SkuDao skuDao;
    @Autowired
    private StockDao stockDao;
    @Autowired
    private AmqpTemplate amqpTemplate;

    @Override
    public PageResult<SpuBo> querySpuByPage(Integer page, Integer rows, Boolean saleable, String key) throws Exception {
        //查询Spu
        //分页,最多允许查询100条数据
        PageHelper.startPage(page,Math.min(rows,100));
        //创建查询条件
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();

        //是否过滤上下架
        if (saleable != null){
            criteria.orEqualTo("saleable",saleable);
        }
        //是否模糊查询
        if (StringUtils.isNotBlank(key)){
            criteria.andLike("title","%"+key+"%");
        }
        Page<Spu> pageInfo = (Page<Spu>) spuDao.selectByExample(example);

        List<SpuBo> list = pageInfo.getResult().stream().map(spu -> {
            //将spu对象转换成spuBo对象
            SpuBo spuBo = new SpuBo();
            //属性拷贝
            BeanUtils.copyProperties(spu,spuBo);

            //2.查询spu的商品分类名称,要查三级分类

            List<String> names = null;
            try {
                names = categoryService.queryNameByIds(Arrays.asList(spu.getCid1(),spu.getCid2(),spu.getCid3()));
            } catch (Exception e) {
                log.error(e.getMessage(),e);
            }
            //将分类名称拼接后存入
            spuBo.setCname(StringUtils.join(names,"/"));

            //3.查询spu的品牌名称
            Brand brand = brandDao.selectByPrimaryKey(spu.getBrandId());
            spuBo.setBname(brand.getName());
            return spuBo;
        }).collect(Collectors.toList());
        return new PageResult<>(pageInfo.getTotal(),list);
    }

    @Override
    @Transactional
    public void saveGoods(SpuBo spuBo)throws Exception {
        //保存Spu
        spuBo.setSaleable(true);
        spuBo.setValid(true);
        spuBo.setCreateTime(new Date());
        spuBo.setLastUpdateTime(spuBo.getCreateTime());
        spuDao.insert(spuBo);
        //保存spu详情
        spuBo.getSpuDetail().setSpuId(spuBo.getId());
        spuDetailDao.insert(spuBo.getSpuDetail());
        //保存Sku 和 库存信息
        saveSkuAndStock(spuBo.getSkus(),spuBo.getId());
        //发送消息到rabbitmq
        this.sendMessage(spuBo.getId(),"insert");
    }

    private void saveSkuAndStock(List<Sku> skus, Long spuId) throws Exception{
        for (Sku sku : skus) {
            if (!sku.getEnable()) {
                continue;
            }
            // 保存sku
            sku.setSpuId(spuId);
            // 初始化时间
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            skuDao.insert(sku);

            // 保存库存信息
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            stockDao.insert(stock);
        }
    }

    @Override
    public SpuDetail querySpuDetailById(Long id)throws Exception {
        return spuDetailDao.selectByPrimaryKey(id);
    }

    @Override
    public List<Sku> querySkuBySpuId(Long id) throws Exception{
        //查询sku
        Sku sku = new Sku();
        sku.setSpuId(id);
        List<Sku> list = skuDao.select(sku);
        //同时查询库存
        list.forEach(s -> {
            s.setStock(stockDao.selectByPrimaryKey(s.getId()).getStock());
        });
        return list;
    }

    @Override
    @Transactional
    public void updateGoods(SpuBo spuBo) throws Exception {
        //查询以前的skus
        List<Sku> skuList = querySkuBySpuId(spuBo.getId());
        //如果以前存在,则删除
        if (!CollectionUtils.isEmpty(skuList)){
            //如果不为空,则将skuList中的skuId 映射到新的集合中
            List<Long> skuIds = skuList.stream().map(s -> s.getId()).collect(Collectors.toList());
            //删除以前的库存
            Example example = new Example(Stock.class);
            example.createCriteria().andIn("skuId",skuIds);
            stockDao.deleteByExample(example);
            //删除以前的sku
            Sku sku = new Sku();
            sku.setSpuId(spuBo.getId());
            skuDao.delete(sku);
        }
        //新增sku 和 库存
        saveSkuAndStock(spuBo.getSkus(), spuBo.getId());
        //更新spu
        spuBo.setLastUpdateTime(new Date());
        spuBo.setCreateTime(null);
        spuBo.setValid(null);
        spuBo.setSaleable(null);
        spuDao.updateByPrimaryKeySelective(spuBo);

        // 更新spu详情
        spuDetailDao.updateByPrimaryKeySelective(spuBo.getSpuDetail());

        //发送消息到rabbitmq中
        this.sendMessage(spuBo.getId(),"update");
    }

    @Override
    @Transactional
    public void deleteGoods(Long id) throws Exception {
        //查询以前的skus
        List<Sku> skuList = querySkuBySpuId(id);
        //如果以前存在,则删除
        if (!CollectionUtils.isEmpty(skuList)){
            //如果不为空,则将skuList中的skuId 映射到新的集合中
            List<Long> skuIds = skuList.stream().map(s -> s.getId()).collect(Collectors.toList());
            //删除以前的库存
            Example example = new Example(Stock.class);
            example.createCriteria().andIn("skuId",skuIds);
            stockDao.deleteByExample(example);
            //删除以前的sku
            Sku sku = new Sku();
            sku.setSpuId(id);
            skuDao.delete(sku);
        }
        //删除spu
        spuDao.deleteByPrimaryKey(id);
        //删除spuDetail
        spuDetailDao.deleteByPrimaryKey(id);

        //发送消息到 rabbitmq中
        this.sendMessage(id,"delete");
    }

    @Override
    public Spu querySpuById(Long id) throws Exception {
        return spuDao.selectByPrimaryKey(id);
    }

    public void sendMessage(Long id,String type){
        //发送消息
        try {
            amqpTemplate.convertAndSend("item."+type , id);
        } catch (AmqpException e) {
           log.error("{}商品信息发送异常,商品id: {}",type,id,e);
        }
    }
}
