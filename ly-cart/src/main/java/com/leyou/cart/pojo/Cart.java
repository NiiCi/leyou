package com.leyou.cart.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Data
@Log4j2
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 商品id
     */
    private Long skuId;
    private String title;
    private String image;// 图片
    private Long price;// 加入购物车时的价格
    private Integer num;// 购买数量
    private String ownSpec;// 商品规格参数


}
