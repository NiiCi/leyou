package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.sevice.BrandService;
import com.leyou.item.sevice.CategoryService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import pojo.Brand;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/brand")
@Log4j2
public class BrandController {
    @Autowired
    private BrandService brandService;
    @Autowired
    private CategoryService categoryService;

    @GetMapping(value = "/page")
    public ResponseEntity<PageResult<Brand>> queryBrandByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "desc", defaultValue = "false") Boolean desc,
            @RequestParam(value = "key", required = false) String key) {
        try {
            PageResult<Brand> pageResult = brandService.queryBrandByPageAndSort(page, rows, sortBy, desc, key);
            if (pageResult == null || pageResult.getItems().size() == 0) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return  ResponseEntity.ok(pageResult);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    @PostMapping
    public ResponseEntity<Void> saveBrand(Brand brand,@RequestParam(value = "cids") List<Long> cids){
        try {
            log.info(brand.toString());
            brandService.saveBrand(brand,cids);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(null);
    }

    @PutMapping
    public ResponseEntity<Void> updateBrand(Brand brand, @RequestParam("cids") List<Long> cids){
        try {
            brandService.updateBrand(brand,cids);
            /*log.info(brand.toString());*/
            /*cids.stream().forEach((s)->{
                log.info(s);
            });*/
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(null);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteBrand(Long bid){
        if (bid == null){
            return ResponseEntity.noContent().build();
        }
        log.info(bid);
        try {
            brandService.deleteBrand(bid);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(null);
    }

    @GetMapping("/cid/{id}")
    public ResponseEntity<List<Brand>> queryBrandByCid(@PathVariable("id") Long cid){
        List<Brand> list = null;
        try {
            list = new ArrayList<>();
            list = brandService.queryBrandByCid(cid);
            if (list == null){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return ResponseEntity.badRequest().build();
        }
        return  ResponseEntity.ok(list);
    }

    @GetMapping("list")
    public ResponseEntity<List<Brand>> queryBrandByIds(@RequestParam("ids") List<Long> ids){
        List<Brand> list = brandService.queryBrandByIds(ids);
        if (CollectionUtils.isEmpty(list)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);
    }
}
