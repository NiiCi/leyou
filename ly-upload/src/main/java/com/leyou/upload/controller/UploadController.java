package com.leyou.upload.controller;

import com.leyou.upload.service.UploadService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/upload")
@Log4j2
public class UploadController {
    @Autowired
    private UploadService uploadService;

    @PostMapping(value = "/image")
    public ResponseEntity<String> uploadImage(MultipartFile file){
        try {
           String url = uploadService.upload(file);
           if (StringUtils.isEmpty(url)){
               return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
           }
           return ResponseEntity.ok(url);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return ResponseEntity.badRequest().build();
    }
}
