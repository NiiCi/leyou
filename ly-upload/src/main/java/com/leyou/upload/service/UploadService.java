package com.leyou.upload.service;

import org.springframework.web.multipart.MultipartFile;

public interface UploadService {
    public String upload(MultipartFile file) throws Exception;
}
