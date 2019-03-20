package com.leyou.page.controller;

import com.leyou.page.service.GoodsService;
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

    @GetMapping("{id}.html")
    public String toItemPage(Model model, @PathVariable("id") Long id){
        //加载所需的数据
        try {
            Map<String,Object> modelMap = goodsService.loadModel(id);
            log.info(modelMap.entrySet());
            model.addAttribute(modelMap);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return "item";
    }
}
