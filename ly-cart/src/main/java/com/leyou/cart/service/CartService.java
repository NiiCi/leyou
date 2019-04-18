package com.leyou.cart.service;

import com.leyou.cart.pojo.Cart;

import java.util.List;

public interface CartService {
    public void addCart(Cart cart) throws Exception;

    public List<Cart> queryCartList() throws Exception;

    public void updateCartNum(Long skuId, Integer num) throws Exception;

    public void deleteCart(Long skuId) throws Exception;
}
