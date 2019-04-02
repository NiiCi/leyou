package com.leyou.search.listener;

import com.leyou.search.service.SearchService;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

@Component
@Log4j2
public class GoodsListener {
    @Resource
    private SearchService searchService;

    @RabbitListener(
            bindings = @QueueBinding(
                    //绑定队列
                    value = @Queue(value = "ly.create.index.queue",durable = "true"),
                    //绑定交换机,交换机默认持久化，类型为  订阅模式
                    exchange = @Exchange(value = "ly.item.exchange",ignoreDeclarationExceptions = "true",type = ExchangeTypes.TOPIC),
                    //路由规则
                    key = {"item.create","item.update"}
            )
    )
    public void listenCreateOrUpdate(Long id) throws Exception {
        if (id == null){
            return;
        }
        System.out.println("接收到消息"+id);
        //创建或更新索引
        searchService.createIndex(id);
    }


    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "ly.delete.index.queue",durable = "true"),
                    exchange = @Exchange(value = "ly.item.exchange",ignoreDeclarationExceptions = "true",type = ExchangeTypes.TOPIC),
                    key = "item.delete"
            )
    )
    public void listenDelete(Long id) throws Exception {
        System.out.println("接收到消息"+id);
        searchService.deleteIndex(id);
    }
}
