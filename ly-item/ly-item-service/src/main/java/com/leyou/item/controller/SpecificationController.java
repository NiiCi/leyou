package com.leyou.item.controller;

import com.leyou.item.sevice.SpecificationService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pojo.SpecGroup;
import pojo.SpecParam;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "spec")
@Log4j2
public class SpecificationController {
    @Autowired
    private SpecificationService specificationService;

    @GetMapping("/groups/{cid}")
    public ResponseEntity<List<SpecGroup>> querySpecGroups(@PathVariable("cid") Long cid ){
        List<SpecGroup> list = new ArrayList<>();
        try {
            list = specificationService.querySpecGroups(cid);
             if (list == null || list.size() <= 0){
                 return new ResponseEntity<>(HttpStatus.NOT_FOUND);
             }
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(list);
    }

    @PostMapping("/group")
    public ResponseEntity<Void> saveSpecGroup(SpecGroup specGroup){
        try {
            specificationService.insertSpecGroup(specGroup);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(null);
    }

    @PutMapping("/group")
    public ResponseEntity<Void> updateSpecGroup(SpecGroup specGroup){
        log.info("开始");
        if (specGroup == null){
            return ResponseEntity.notFound().build();
        }
        log.info(specGroup.toString());
        try {
            specificationService.updateSpecGroup(specGroup);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(null);
    }
    @DeleteMapping("/group/{id}")
    public ResponseEntity<Void> deleteSpecGroup(@PathVariable("id")  Long id){
        try {
            specificationService.deleteSpecGroup(id);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(null);
    }

    @GetMapping("/params")
    public ResponseEntity<List<SpecParam>> querySpecParams(
            @RequestParam(value="gid",required = false) Long gid,
            @RequestParam(value="cid",required = false) Long cid,
            @RequestParam(value="searching",required = false) Boolean searching,
            @RequestParam(value="generic",required = false) Boolean generic) {
        List<SpecParam> list = new ArrayList<>();
        try {
            list = specificationService.querySpecParams(gid,cid,searching,generic);
            if (list == null || list.size() <= 0) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(list);
    }

    @PostMapping("/param")
    public ResponseEntity<Void> saveSpecParam(SpecParam specParam){
        try {
            specificationService.insertSpecParam(specParam);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(null);
    }

    @PutMapping("/param")
    public ResponseEntity<Void> updateSpecParam(SpecParam specParam){
        try {
            specificationService.updateSpecParam(specParam);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(null);
    }
    @DeleteMapping("/param/{id}")
    public ResponseEntity<Void> deleteSpecParam(@PathVariable("id")  Long id){
        try {
            specificationService.deleteSpecParam(id);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(null);
    }
}
