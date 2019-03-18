package com.leyou.upload.service.impl;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.leyou.upload.service.UploadService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service(value = "uploadService")
@Log4j2
public class UploadServiceImpl implements UploadService {
    // 支持的文件类型
    private static final List<String> suffixes = Arrays.asList("image/png", "image/jpeg");

    //FastDFS客户端
    @Autowired
    private FastFileStorageClient storageClient;

    @Override
    public String upload(MultipartFile file) throws Exception{
        //1.图片信息校验
        //1)校验图片类型
        try {
            String type = file.getContentType();
            if (!suffixes.contains(type)) {
                log.error("上传图片失败,文件类型不匹配:{}", type);
                return null;
            }
            //2)校验图片内容
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null){
                log.error("上传图片失败,文件内容不符合要求!");
                return null;
            }

            //2.将图片上传到FastDFS
            //1)获取文件后缀名
            String extension = StringUtils.substringAfterLast(file.getOriginalFilename(),".");
            //2)上传
            StorePath storePath = storageClient.uploadFile(file.getInputStream(),file.getSize(),extension,null);
            //3)返回完整路径
            return "http://image.leyou.com/"+storePath.getFullPath();

            /*//2.保存图片
            //1)生成保存目录
            File dir = new File("E:\\leyouUpload");
            //如果文件夹不存在则创建文件夹
            if(!dir.exists()){
                dir.mkdirs();
            }
            //2)保存图片
            //转存文件,保存格式为当前时间.jpg,保存在leyouUpload目录下
            file.transferTo(File.createTempFile(String.valueOf(System.currentTimeMillis()),"jpg",dir));
            file.transferTo(new File(dir,file.getOriginalFilename()));
            //3)拼接图片地址
            String url = "http://image.leyou.com/upload/"+file.getOriginalFilename();
            return url;*/
        } catch (IOException e) {
            log.error(e.getMessage(),e);
            return null;
        }
    }
}
