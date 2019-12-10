package com.funnycode.blog.async.producer;

import com.alibaba.fastjson.JSONObject;
import com.funnycode.blog.async.EventModel;
import com.funnycode.blog.async.EventProducer;
import com.funnycode.blog.configration.RabbitMQConfiguration;
import com.funnycode.blog.util.MQKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;

/**
 * @author CC
 * @date 2019-09-23 10:47
 */
@Service
public class MessageEventProducer implements EventProducer, RabbitTemplate.ConfirmCallback , RabbitTemplate.ReturnCallback{

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageEventProducer.class);

    private RabbitTemplate rabbitTemplate;

    @Autowired
    public MessageEventProducer(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitTemplate.setConfirmCallback(this);
        this.rabbitTemplate.setReturnCallback(this);
    }

    @Override
    public boolean sendEvent(EventModel eventModel) {
        try {
            String msg = JSONObject.toJSONString(eventModel);
            String routingKey = MQKeyUtil.getRoutingKey(eventModel.getType());
            CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
            rabbitTemplate.convertAndSend(RabbitMQConfiguration.EXCHANGE_BLOG, routingKey, msg, correlationId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean b, String s) {
        if(b){
            LOGGER.info("消息队列发送成功：{}", correlationData);
        }else{
            LOGGER.warn("消息队列发送失败：{}", correlationData);
        }
    }

    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingkey) {
        LOGGER.warn("消息投递队列失败：{0}", message);
        LOGGER.warn("返回值：{0}，返回信息：{1}", replyCode, replyText);
        LOGGER.warn("消息路径：{0} --> {1}", exchange, routingkey);
    }
}
