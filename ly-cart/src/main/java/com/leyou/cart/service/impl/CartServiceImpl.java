package com.leyou.cart.service.impl;

import com.leyou.auth.pojo.UserInfo;
import com.leyou.cart.client.GoodsClient;
import com.leyou.cart.interceptor.LoginInterceptor;
import com.leyou.cart.pojo.Cart;
import com.leyou.cart.service.CartService;
import com.leyou.common.utils.JsonUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import pojo.Sku;

import java.util.List;

@Service("cartService")
@Log4j2
public class CartServiceImpl implements CartService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private GoodsClient goodsClient;
    static final String KEY_PREFIX = "ly:cart:uid:";

    //先查询之前的购物车数据
    //判断要添加的商品是否存在
    //存在，则直接修改数量后写入redis
    //不存在，新增一条数据，然后写入redis
    @Override
    public void addCart(Cart cart) throws Exception {
        //获取登录用户
        UserInfo userInfo = LoginInterceptor.getLoginUser();
        //redis 中的key
        String key = KEY_PREFIX + userInfo.getId();
        //获取hash操作对象
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(key);
        //查询是否存在
        Long skuId = cart.getSkuId();
        Integer num = cart.getNum();
        // 判断商品是否存在
        Boolean flag = hashOps.hasKey(skuId.toString());
        if (flag) {
            //存在，获取购物车数据
            String json = hashOps.get(skuId.toString()).toString();
            cart = JsonUtils.parse(json, Cart.class);
            //修改购物车数量
            cart.setNum(cart.getNum() + num);
        } else {
            //不存在，新增购物车
            cart.setUserId(userInfo.getId());
            //其他商品信息，需要查询的商品服务
            Sku sku = goodsClient.querySkuById(skuId);
            log.info(JsonUtils.serialize(sku));
            cart.setImage(sku.getImages());
            cart.setPrice(sku.getPrice());
            cart.setTitle(sku.getTitle());
            cart.setOwnSpec(sku.getOwnSpec());
        }
        //将购物车数据写入redis
        hashOps.put(cart.getSkuId().toString(), JsonUtils.serialize(cart));
    }

    @Override
    public List<Cart> queryCartList() throws Exception {
        //从线程域从获取用户信息
        UserInfo userInfo = LoginInterceptor.getLoginUser();
        //判断是否存在购物车
        String key = KEY_PREFIX+ userInfo.getId();
        if (!redisTemplate.hasKey(key)){
            //不存在，直接返回
            return null;
        }
        BoundHashOperations<String,Object,Object> hashOps = redisTemplate.boundHashOps(key);
        List<Object> carts = hashOps.values();
        //判断是否有数据
        if (CollectionUtils.isEmpty(carts)){
            return null;
        }
    }
}
