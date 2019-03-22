package com.leyou.page.controller;

import com.leyou.page.service.GoodsService;
import com.leyou.page.service.FileService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/item")
@Log4j2
public class GoodsController {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private FileService fileService;
    /**
     * 跳转到商品详情页
     * @param model
     * @param id
     * @return
     */
    @GetMapping("{id}.html")
    public String toItemPage(Model model, @PathVariable("id") Long id){
        //加载所需的数据
        try {
            Map<String,Object> modelMap = goodsService.loadModel(id);
            //放入模型
            model.addAllAttributes(modelMap);
            log.info(model);
            //判断是否需要生成静态化页面
            if (!fileService.exists(id)){
                //异步创建静态化页面
                fileService.syncCreateHtml(id);
            }
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return "item";
    }
}
