package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.sevice.GoodsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pojo.Sku;
import pojo.Spu;
import pojo.SpuBo;
import pojo.SpuDetail;

import java.util.List;
import java.util.Optional;

@RestController
@Log4j2
@RequestMapping
public class GoodsController {
    @Autowired
    private GoodsService goodsService;

    @GetMapping("/spu/page")
    public ResponseEntity<PageResult<SpuBo>> querySpuByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "saleable", required = false) Boolean saleable,
            @RequestParam(value = "key", required = false) String key){
        PageResult<SpuBo> list = null;
        try {
            list = goodsService.querySpuByPage(page,rows,saleable,key);
            if (list == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(list);
    }


    @PostMapping("/spu/page")
    public ResponseEntity<Void> saveGoods(@RequestBody SpuBo spuBo){
        try {
            goodsService.saveGoods(spuBo);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(null);
    }

    @GetMapping("/spu/detail/{id}")
    public ResponseEntity<SpuDetail> querySpuDetailById(@PathVariable("id") Long id){
        SpuDetail detail = null;
        if (id == null){
            return ResponseEntity.badRequest().build();
        }
        try {
            detail = goodsService.querySpuDetailById(id);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return ResponseEntity.badRequest().build();
        }
        if (detail == null){
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.ok(detail);
    }

    @GetMapping("/sku/list")
    public ResponseEntity<List<Sku>> querySkuBySpuId(@RequestParam("id") Long id){
        List<Sku> list = null;
        if (id == null){
            return ResponseEntity.badRequest().build();
        }
        try {
            list = goodsService.querySkuBySpuId(id);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return ResponseEntity.badRequest().build();
        }
        if (list == null || list.size() <= 0){
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.ok(list);
    }

    @PutMapping("/goods")
    public ResponseEntity<Void> updateGoods(@RequestBody SpuBo spuBo)
    {
        try {
            goodsService.updateGoods(spuBo);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/goods/{id}")
    public ResponseEntity<Void> deleteGoods(@PathVariable("id") Long id)
    {
        try {
            goodsService.deleteGoods(id);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(null);
    }

    @GetMapping("spu/{id}")
    public ResponseEntity<Spu> querySpuById(@PathVariable("id") Long id){
        Spu spu = null;
        try {
            spu = goodsService.querySpuById(id);
            if (Optional.ofNullable(spu).isPresent()){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(spu);
    }
}
