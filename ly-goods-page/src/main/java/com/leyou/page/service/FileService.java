package com.leyou.page.service;

import com.leyou.common.utils.ThreadUtils;
import com.leyou.page.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

@Service
public class FileService{
    @Autowired
    private GoodsService goodsService;
    //模板引擎
    @Autowired
    private TemplateEngine templateEngine;
    //thymeleaf 静态页面存放地址
    @Value("${ly.thymeleaf.destPath}")
    private String destPath;

    /**
     * 创建html静态化页面
     * @param id
     * @throws Exception
     */
    public void createHtml(Long id) throws Exception {
        //创建上下文
        Context context = new Context();
        //存放数据
        context.setVariables(goodsService.loadModel(id));
        //创建输出流,关联到一个临时文件
        File temp = new File(id + ".html");
        //目标页面文件
        File dest = createPath(id);
        //备份原页面文件
        File bak = new File(id+"_bak.html");
        try(PrintWriter writer = new PrintWriter(temp,"UTF-8")){
            //利用thymeleaf模板引擎生成 静态页面
            templateEngine.process("item",context,writer);
            if (dest.exists()){
                //如果目标文件已存在，先备份
                dest.renameTo(bak);
            }
            //将新页面覆盖旧页面
            FileCopyUtils.copy(temp,dest);
            //成功后将备份页面删除
            bak.delete();
        }catch (IOException e){
            //失败后，将页面恢复
            bak.renameTo(dest);
            //重新抛出异常,声明页面生成失败
            throw  new Exception(e);
        }finally {
            //删除临时页面
            if (temp.exists()){
                temp.delete();
            }
        }
    }

    /**
     * 创建页面文件
     * @param id
     * @return
     */
    private File createPath(Long id) {
        if (id == null){
            return null;
        }
        //目标文件
        File dest = new File(this.destPath);
        //如果文件夹不存在，则创建
        if (!dest.exists()){
            dest.mkdirs();
        }
        return new File(dest,id+".html");
    }
    /**
     * 判断某个商品的页面是否存在
     * @param id
     * @return
     */
    public boolean exists(Long id){
        return this.createPath(id).exists();
    }

    /**
     * 异步创建html页面
     * @param id
     */
    public void syncCreateHtml(Long id){
        ThreadUtils.execute(() -> {
            try {
                createHtml(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
