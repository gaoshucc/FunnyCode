package com.funnycode.blog.async;

/**
 * @author CC
 * @date 2019-09-23 00:03
 */
public interface EventProducer {

    /**
     * 生产消息
     * @param eventModel 消息实体
     */
    boolean sendEvent(EventModel eventModel);
}
