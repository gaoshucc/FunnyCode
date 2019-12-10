package com.funnycode.blog.async;

import java.util.List;

/**
 * 事件处理器接口
 * @author CC
 * @date 2019-09-23 00:28
 */
public interface EventHandler {
    /**
     * 处理事件
     * @param model 事件
     */
    void doHandle(EventModel model);

    /**
     * 获取处理器支持的事件类型
     * @return 事件类型列表
     */
    List<EventType> getSupportEventTypes();
}
