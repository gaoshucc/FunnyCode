package com.funnycode.blog.async.consumer;

import com.alibaba.fastjson.JSON;
import com.funnycode.blog.async.EventHandler;
import com.funnycode.blog.async.EventModel;
import com.funnycode.blog.async.EventType;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author CC
 * @date 2019-09-23 16:44
 */
public class DefaultConsumer implements ChannelAwareMessageListener, InitializingBean, ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(DefaultConsumer.class);
    private static Map<EventType, List<EventHandler>> config = new HashMap<>();
    private ApplicationContext applicationContext;

    @Override
    public void onMessage(Message message, Channel channel) throws IOException {
        logger.info("=======================正在消费信息========================");
        //todo 处理消息
        try{
            EventModel eventModel = JSON.parseObject(new String(message.getBody()), EventModel.class);
            if (!config.containsKey(eventModel.getType())) {
                logger.error("不能识别的事件");
                return;
            }
            for (EventHandler handler : config.get(eventModel.getType())) {
                handler.doHandle(eventModel);
            }
            logger.info("=====消息处理完成了=====");
            logger.info("DeliveryTag:{}", message.getMessageProperties().getDeliveryTag());
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }catch (Exception e){
            e.printStackTrace();
            if(message.getMessageProperties().getRedelivered()){
                logger.error("消息已重复处理失败,拒绝再次接收...");
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
            }else{
                logger.error("消息即将再次返回队列处理...");
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            }
        }
    }

    @Override
    public void afterPropertiesSet() {
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
        if (beans != null) {
            for (Map.Entry<String, EventHandler> entry : beans.entrySet()) {
                List<EventType> eventTypes = entry.getValue().getSupportEventTypes();

                for (EventType type : eventTypes) {
                    if (!config.containsKey(type)) {
                        config.put(type, new ArrayList<>());
                    }
                    config.get(type).add(entry.getValue());
                }
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
