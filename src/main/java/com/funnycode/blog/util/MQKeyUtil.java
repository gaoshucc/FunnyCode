package com.funnycode.blog.util;

import com.funnycode.blog.async.EventType;
import com.funnycode.blog.configration.RabbitMQConfiguration;

/**
 * @author CC
 * @date 2019-09-23 10:50
 */
public class MQKeyUtil {

    /**
     * 获取消息队列路由键
     * @param eventType 消息类型
     * @return String 路由键
     */
    public static String getRoutingKey(EventType eventType) {
        String routingKey;
        switch (eventType){
            case LIKE:
                routingKey = RabbitMQConfiguration.ROUTING_LIKE;
                break;
            case COMMENT:
                routingKey = RabbitMQConfiguration.ROUTING_COMMENT;
                break;
            case LOGIN:
                routingKey = RabbitMQConfiguration.ROUTING_LOGIN;
                break;
            case MAIL:
                routingKey = RabbitMQConfiguration.ROUTING_MAIL;
                break;
            case FOLLOW:
                routingKey = RabbitMQConfiguration.ROUTING_FOLLOW;
                break;
            case UNFOLLOW:
                routingKey = RabbitMQConfiguration.ROUTING_UNFOLLOW;
                break;
            case LOG:
                routingKey = RabbitMQConfiguration.ROUTING_LOG;
                break;
            case REGIST:
                routingKey = RabbitMQConfiguration.ROUTING_REGIST;
                break;
            case COLLECT:
                routingKey = RabbitMQConfiguration.ROUTING_COLLECT;
                break;
            default:
                routingKey = RabbitMQConfiguration.ROUTING_DEFAULT;
                break;
        }

        return routingKey;
    }
}
