package com.leyou.page.listener;

import com.leyou.page.service.FileService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

@Component
public class GoodsListener {
    @Resource
    private FileService fileService;

    /**
     * 监听商品服务传递的消息，当新增或者修改时执行，创建静态化页面
     * @param id
     * @throws IOException
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "ly.create.page.queue",durable = "true"),
                    exchange = @Exchange(value = "ly.item.exchange",ignoreDeclarationExceptions = "true",type = ExchangeTypes.TOPIC),
                    key = {"item.create","item.update"}
            )
    )
    public void listenCreate(Long id) throws Exception {
        if (id == null){
            return;
        }
        //创建页面，并覆盖
        fileService.createHtml(id);
    }

    /**
     * 监听商品服务传递的消息，当删除时执行，删除静态化页面
     * @param id
     * @throws IOException
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "ly.delete.page.queue",durable = "true"),
                    exchange = @Exchange(value = "ly.item.exchange",ignoreDeclarationExceptions = "true",type = ExchangeTypes.TOPIC),
                    key = {"item.delete"}
            )
    )
    public void listenDelete(Long id)throws IOException{
        if (id == null){
            return;
        }
        fileService.deleteHtml(id);
    }
}
