package com.leyou.item.controller;

import com.leyou.item.sevice.CategoryService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import pojo.Category;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/category")
@Log4j2
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 根据parentId查询类目
     * @param pid
     * @return
     */
    @GetMapping(value = "/list")
    public ResponseEntity<List<Category>> queryCategoryListByParentId(@RequestParam(value = "pid",defaultValue = "0") Long pid){
        if (pid == null || pid.longValue() < 0){
            return ResponseEntity.badRequest().build();
        }
        //执行查询操作
        try {
            List<Category> categoryList = categoryService.queryCategoryListByParentId(pid);
            if (categoryList == null){
                //返回结果集为空,响应 404
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(categoryList);
        }catch (Exception e){
            log.info(e.getMessage(),e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping(value = "bid/{bid}")
    public ResponseEntity<List<Category>> queryByBrandId(@PathVariable("bid") Long bid){
        List<Category>  list = new ArrayList<>();
        try {
            list = categoryService.queryByBrandId(bid);
            if(list == null || list.size() < 1){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/names")
    public ResponseEntity<List<String>> queryNameByIds(@RequestParam("ids") List<Long> ids){
        List<String> list = null;
        try {
           list = categoryService.queryNameByIds(ids);
           if (CollectionUtils.isEmpty(list)){
               return new ResponseEntity<>(HttpStatus.NOT_FOUND);
           }
        } catch (Exception e) {
           log.error(e.getMessage(),e);
           return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("all/level")
    public ResponseEntity<List<Category>> queryAllByCid3(@RequestParam("id") Long id){
        List<Category> list = new ArrayList<>();
        try {
            list = categoryService.queryAllByCid3(id);
            if (list == null || list.size() < 1){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return ResponseEntity.ok(list);
    }
}


